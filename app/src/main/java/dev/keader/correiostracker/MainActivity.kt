package dev.keader.correiostracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dev.keader.correiostracker.databinding.ActivityMainBinding
import dev.keader.correiostracker.home.HomeFragmentDirections
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpNavigation()

        binding.floatingActionButton.setOnClickListener { view ->
            //binding.bottomNavigation.visibility = View.GONE
            view.findNavController().navigate(HomeFragmentDirections.actionGlobalAddPacketFragment())
        }


        // example of how navigate
        //navController.navigate(HomeFragmentDirections.actionHomeFragmentToTrackDetailFragment("1234"))

    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigation, navHostFragment.navController)
        Navigation.setViewNavController(binding.root, navHostFragment.navController)
    }
}