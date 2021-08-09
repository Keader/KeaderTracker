package dev.keader.correiostracker

import android.app.ActionBar
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.ActivityMainBinding
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.home.HomeFragmentDirections
import dev.keader.correiostracker.work.RefreshTracksWorker
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val uiViewModel: UIViewModel by viewModels()
    private val navController
        get() = findNavController(R.id.nav_host_fragment)
    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.floatingActionButton.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionGlobalAddPacketFragment())
        }

        uiViewModel.bottomNavVisibility.observe(this, { visibility ->
            binding.bottomNavigation.visibility = visibility
            binding.floatingActionButton.visibility = visibility
        })

        uiViewModel.frequency.distinctUntilChanged().observe(this, {
            RefreshTracksWorker.stopWorker(this)
            RefreshTracksWorker.startWorker(this, preferences)
        })

        uiViewModel.darkTheme.observe(this, { darkTheme ->
            AppCompatDelegate.setDefaultNightMode(
                if (darkTheme)
                    MODE_NIGHT_YES
                else
                    MODE_NIGHT_NO
            )
        })

        binding.root.doOnLayout {
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
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
