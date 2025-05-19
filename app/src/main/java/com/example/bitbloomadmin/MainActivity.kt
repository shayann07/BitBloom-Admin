package com.example.bitbloomadmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bitbloomadmin.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        // Edge-to-edge if you want your content under status/nav bars
        enableEdgeToEdge()

        // Inflate with ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val navController = findNavController(R.id.nav_host_fragment)

        binding.bottomNav.setupWithNavController(navController)

        // ...then override it with manual listeners:
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    navController.navigate(R.id.dashboardFragment)
                    true
                }

                R.id.nav_users -> {
                    navController.navigate(R.id.usersFragment)
                    true
                }

                R.id.nav_plans -> {
                    navController.navigate(R.id.planFragment)
                    true
                }

                R.id.nav_reports -> {
                    navController.navigate(R.id.annoucementFragment)
                    true
                }

                R.id.nav_withdrawals -> {
                    navController.navigate(R.id.withdrawFragment)
                    true
                }

                else -> false
            }
        }
    }
}