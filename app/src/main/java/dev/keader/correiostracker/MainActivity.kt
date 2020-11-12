package dev.keader.correiostracker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.ActivityMainBinding
import dev.keader.correiostracker.view.home.HomeFragmentDirections
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private val _uiViewModel : UIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpNavigation()

        binding.floatingActionButton.setOnClickListener { view ->
            view.findNavController().navigate(HomeFragmentDirections.actionGlobalAddPacketFragment())
        }

        _uiViewModel.bottomNavVisibility.observe(this, { visibility ->
            binding.bottomNavigation.visibility = visibility
            binding.floatingActionButton.visibility = visibility
        })

        // example of how navigate
        //navController.navigate(HomeFragmentDirections.actionHomeFragmentToTrackDetailFragment("1234"))

    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigation, navHostFragment.navController)
        Navigation.setViewNavController(binding.root, navHostFragment.navController)
    }
}