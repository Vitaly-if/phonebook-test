package com.example.phonebooktestapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.phonebooktestapp.R
import com.example.phonebooktestapp.databinding.ActivityMainBinding
import androidx.appcompat.app.ActionBarDrawerToggle as ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupNavigation()


    }

    private fun setupNavigation() {
      //  val navController = this.findNavController(R.id.myNavHostFragment)
        //setSupportActionBar(binding.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        appBarConfiguration.fallbackOnNavigateUpListener
        // then setup the action bar, tell it about the DrawerLayout
       // setupActionBarWithNavController(navController, binding.drawerLayout)


//        val toolBar = supportActionBar
//        val drawerToogle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
//            this,
//            binding.drawerLayout,
//            R.string.open,
//            R.string.close
//        ) {
//
//        }
//        //drawerToogle.isDrawerIndicatorEnabled = true
//        binding.drawerLayout.addDrawerListener(drawerToogle)
//        drawerToogle.syncState()
//
//        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout);
//
//     //  binding.navigationView.setNavigationItemSelectedListener(this)
//
//        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when(destination.id) {
                R.id.detailFragment -> {
                    toolBar.setDisplayShowTitleEnabled(false)
                    //binding.heroImage.visibility = View.VISIBLE
                    Log.i(ContentValues.TAG, "проверка драйвера")
                }
                else -> {
                    toolBar.setDisplayShowTitleEnabled(false)
                    toolBar.setDisplayHomeAsUpEnabled(true)
                    toolBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
                  //  binding.heroImage.visibility = View.GONE
                }
            }
        }


    }

//    fun openCloseNavigationDrawer() {
//        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            binding.drawerLayout.close()
//            Log.i(ContentValues.TAG, "Закрытие драйвера")
//        } else {
//            binding.drawerLayout.openDrawer(GravityCompat.START)
//            Log.i(ContentValues.TAG, "Открытие драйвера")
//        }
//    }


    // стрелка назад
//  override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.myNavHostFragment)
//      //
//        return navController.navigateUp() || super.onSupportNavigateUp()
//   }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.myNavHostFragment)

        //openCloseNavigationDrawer()
        return navController.navigateUp() || super.onSupportNavigateUp()
        //return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}