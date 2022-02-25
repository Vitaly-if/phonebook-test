package com.example.phonebooktestapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.phonebooktestapp.databinding.ActivityMainBinding
import com.example.phonebooktestapp.managers.PhotoManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupNavigation()

    }

    private fun setupNavigation() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        appBarConfiguration.fallbackOnNavigateUpListener

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when (destination.id) {
                R.id.detailFragment -> {
                    toolBar.setDisplayShowTitleEnabled(false)
                }
                else -> {
                    toolBar.setDisplayShowTitleEnabled(false)
                    toolBar.setDisplayHomeAsUpEnabled(true)
                    toolBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
        PhotoManager.getInstance(baseContext)
        PhotoManager.onActivityResult(requestCode, resultCode, data)
        }

    override fun onSupportNavigateUp(): Boolean {

        val navController = findNavController(R.id.myNavHostFragment)

        return navController.navigateUp() || super.onSupportNavigateUp()

    }


}