package com.example.movieseries

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.movieseries.databinding.ActivityMainBinding
import com.example.movieseries.viewmodel.HomeViewModel
import com.example.movieseries.viewmodel.HomeViewModelFactory
import com.example.movieseries.data.MovieRepository
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, HomeViewModelFactory(MovieRepository(this)))[HomeViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfig = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.savedFragment),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfig)
        binding.bottomNav.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)

//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Movies"))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Series"))
//
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                when (tab.position) {
//                    0 -> navController.navigate(R.id.homeFragment)
//                    1 -> navController.navigate(R.id.seriesFragment)
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // TODO: Trigger search here using viewModel.searchText.value = query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // If you want real-time search, you can also bind it to the ViewModel here
                return true
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
