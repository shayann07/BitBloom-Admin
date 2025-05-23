package com.example.bitbloomadmin

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bitbloomadmin.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Force dark mode if desired
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        // 2) NavController
        val navController = findNavController(R.id.nav_host_fragment)




        // 3) BottomNav <-> NavController
        binding.bottomNavBar.setupWithNavController(navController)

        // If you prefer manual navigation (you had it before), keep it:
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard   -> navController.navigate(R.id.dashboardFragment)
                R.id.nav_users       -> navController.navigate(R.id.usersFragment)
                R.id.nav_plans       -> navController.navigate(R.id.planFragment)
                R.id.nav_reports     -> navController.navigate(R.id.reportFragment)
                R.id.nav_withdrawals -> navController.navigate(R.id.withdrawFragment)
                else                 -> false
            }.let { true }
        }

        // 4) DrawerToggle (hamburger icon)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 5) NavigationView item clicks
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // close drawer first
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_dashboard        -> navController.navigate(R.id.dashboardFragment)
                R.id.nav_users       -> navController.navigate(R.id.usersFragment)
                R.id.nav_plans       -> navController.navigate(R.id.planFragment)
                R.id.nav_reports     -> navController.navigate(R.id.reportFragment)
                R.id.nav_withdrawals -> navController.navigate(R.id.withdrawFragment)
                R.id.nav_announcements -> navController.navigate(R.id.annoucementFragment)
            }
            true
        }
    }

    fun openDrawer() = binding.drawerLayout.openDrawer(GravityCompat.START)
    override fun onSupportNavigateUp(): Boolean {
        // ensure the hamburger toggles/Back arrow behaves correctly
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
