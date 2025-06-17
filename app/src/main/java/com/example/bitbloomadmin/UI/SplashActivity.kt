package com.example.bitbloomadmin.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.LoginActivity
import com.example.bitbloomadmin.MainActivity
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.databinding.ActivitySplashBinding
import com.example.bitbloomadmin.utils.SharedPrefManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)




        // Initialize Room, Firebase, and ViewModel
        val dao = AppDatabase.getDatabase(this).userDao()
        val repository = UserRepository(FirebaseHelper(this), dao)
        val factory = UserViewModelFactory(repository)
        val sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        val isLoggedIn = SharedPrefManager(this).isLoggedIn()
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // Fetch and sync data from Firebase to Room
        lifecycleScope.launch {
            viewModel.syncNow() // suspend function from UserViewModel
            delay(1500) // Give user time to see the logo + loading animation
            // Proceed to actual app screen (e.g., MainActivity)
            if (isLoggedIn) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }
}