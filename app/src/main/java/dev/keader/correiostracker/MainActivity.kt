package dev.keader.correiostracker

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.ActivityMainBinding
import dev.keader.correiostracker.view.home.HomeFragmentDirections


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val _uiViewModel: UIViewModel by viewModels()
    private val navController
        get() = findNavController(R.id.nav_host_fragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.floatingActionButton.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionGlobalAddPacketFragment())
        }

        _uiViewModel.bottomNavVisibility.observe(this, { visibility ->
            binding.bottomNavigation.visibility = visibility
            binding.floatingActionButton.visibility = visibility
        })

        binding.root.doOnLayout {
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val code = result.contents
            _uiViewModel.setQrCode(code)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getSnackInstance(string: String, duration: Int): Snackbar {
        val snackbar = Snackbar.make(binding.root, string, duration)
        val layoutParams = ActionBar.LayoutParams(snackbar.view.layoutParams)
        layoutParams.gravity = Gravity.TOP
        snackbar.view.setPadding(0, 10, 0, 0)
        snackbar.view.layoutParams = layoutParams
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        return snackbar
    }
}
