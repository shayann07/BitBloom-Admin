package com.example.bitbloomadmin.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bitbloom.bitbloomadmin.utils.Utils
import com.example.bitbloomadmin.Data.local.AppDatabase
import com.example.bitbloomadmin.Data.remote.FirebaseHelper
import com.example.bitbloomadmin.MainActivity
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.Repository.UserRepository
import com.example.bitbloomadmin.Viewmodel.UserViewModel
import com.example.bitbloomadmin.Viewmodel.UserViewModelFactory
import com.example.bitbloomadmin.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel
    private lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show logo and animation immediately
        utils = Utils(this)
        utils.startLoadingAnimation()

        // Initialize Room, Firebase, and ViewModel
        val dao = AppDatabase.getDatabase(this).userDao()
        val repository = UserRepository(FirebaseHelper(this), dao)
        val factory = UserViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // Fetch and sync data from Firebase to Room
        lifecycleScope.launch {
            viewModel.syncNow() // suspend function from UserViewModel
            delay(1500) // Give user time to see the logo + loading animation

            utils.endLoadingAnimation()

            // Proceed to actual app screen (e.g., MainActivity)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}