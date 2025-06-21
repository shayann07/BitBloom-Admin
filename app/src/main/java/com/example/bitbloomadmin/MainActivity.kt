package com.example.bitbloomadmin

// WorkManager imports (must be these exact packages):

// Your SyncWorker (ensure package matches where you placed it):

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bitbloomadmin.databinding.ActivityMainBinding
import com.example.bitbloomadmin.workers.SyncWorker
import com.google.firebase.FirebaseApp
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Schedule the daily sync before setting the content view
        setupDailySync()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Force dark mode if desired
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Set up Navigation
        val navController = findNavController(R.id.nav_host_fragment)

        // BottomNav <-> NavController
        binding.bottomNavBar.setupWithNavController(navController)
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> navController.navigate(R.id.dashboardFragment)
                R.id.nav_users -> navController.navigate(R.id.usersFragment)
                R.id.nav_plans -> navController.navigate(R.id.planFragment)
                R.id.nav_reports -> navController.navigate(R.id.reportFragment)
                R.id.nav_withdrawals -> navController.navigate(R.id.withdrawFragment)
                else -> false
            }
            true
        }

        // DrawerToggle (hamburger icon)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView item clicks
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_dashboard -> navController.navigate(R.id.dashboardFragment)
                R.id.nav_users -> navController.navigate(R.id.usersFragment)
                R.id.nav_plans -> navController.navigate(R.id.planFragment)
                R.id.nav_reports -> navController.navigate(R.id.reportFragment)
                R.id.nav_withdrawals -> navController.navigate(R.id.withdrawFragment)
                R.id.nav_announcements -> navController.navigate(R.id.annoucementFragment)
                R.id.nav_announcementsPoster -> navController.navigate(R.id.addPosterFragment)
                R.id.nav_support -> navController.navigate(R.id.supportFragment)
                R.id.top_leaders -> navController.navigate(R.id.topLeadersFragment)
            }
            true
        }
    }

    private fun setupDailySync() {
        // 1) Build Constraints (only network requirement)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 2) Build a PeriodicWorkRequest (every 24 hours),
        //    delaying first run until next 2:00 AM local time
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateDelayUntil(2, 0), TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1L,
                TimeUnit.MINUTES
            )
            .setConstraints(constraints)
            .build()

        // 3) Enqueue the unique periodic work
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "daily_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
    }

    /**
     * Compute milliseconds until the next occurrence of [hour:minute] local time.
     * If it's already past that time today, schedule for tomorrow.
     */
    private fun calculateDelayUntil(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = now.clone() as Calendar
        target.set(Calendar.HOUR_OF_DAY, hour)
        target.set(Calendar.MINUTE, minute)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    fun openDrawer() = binding.drawerLayout.openDrawer(GravityCompat.START)

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
