package com.example.bitbloomadmin

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitbloomadmin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var prefs: SharedPreferences
    private lateinit var auth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        // Make sure this matches the prefs name used elsewhere
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        auth  = FirebaseAuth.getInstance()

        // Show/Hide password toggle
        binding.passwordEditText.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                val bounds = binding.passwordEditText.compoundDrawables[DRAWABLE_RIGHT].bounds
                if (event.rawX >= binding.passwordEditText.right - bounds.width()) {
                    val isPasswordVisible =
                        binding.passwordEditText.transformationMethod is HideReturnsTransformationMethod
                    if (isPasswordVisible) {
                        binding.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.baseline_remove_red_eye_24, 0
                        )
                    } else {
                        binding.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.baseline_visibility_off_24, 0
                        )
                    }
                    binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.loginButton.setOnClickListener {
            val email    = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Email and password are required")
                return@setOnClickListener
            }

            // Sign in with FirebaseAuth
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val firebaseUid = auth.currentUser!!.uid

                    // Save uid locally
                    prefs.edit()
                        .putString("adminId", firebaseUid)
                        .apply()

                    // Query the Admin collection by email
                    firestore.collection("Admin")
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (snapshot.isEmpty) {
                                showMessage("No admin record found for $email")
                                return@addOnSuccessListener
                            }

                            // Get the real DocumentReference
                            val docRef = snapshot.documents[0].reference

                            // Fetch FCM token and update both id and deviceToken
                            FirebaseMessaging.getInstance().token
                                .addOnSuccessListener { token ->
                                    val updates = mapOf(
                                        "id" to firebaseUid,
                                        "deviceToken" to token
                                    )
                                    docRef.update(updates)
                                        .addOnSuccessListener {
                                            showMessage("Login successful!")
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            showMessage("Failed to save token: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    showMessage("FCM token error: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            showMessage("Admin lookup failed: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    showMessage("Authentication failed: ${e.localizedMessage}")
                }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
