package dev.keader.correiostracker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.ActivityMainBinding
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.model.windowInsetsControllerCompat
import dev.keader.correiostracker.view.home.HomeFragmentDirections
import dev.keader.correiostracker.work.RefreshTracksWorker
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val uiViewModel: UIViewModel by viewModels()
    private val navController
        get() = findNavController(R.id.nav_host_fragment)
    @Inject
    lateinit var preferences: PreferencesManager

    companion object {
        const val UPDATE_CODE = 584
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            checkAppUpdate()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.floatingActionButton.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionGlobalAddPacketFragment())
        }

        uiViewModel.bottomNavVisibility.observe(this) { visibility ->
            binding.bottomNavigation.visibility = visibility
            binding.floatingActionButton.visibility = visibility
        }

        uiViewModel.frequency.observe(this) {
            RefreshTracksWorker.stopWorker(this)
            RefreshTracksWorker.startWorker(this, preferences)
        }

        uiViewModel.darkTheme.observe(this) { darkTheme ->
            AppCompatDelegate.setDefaultNightMode(
                if (darkTheme)
                    MODE_NIGHT_YES
                else
                    MODE_NIGHT_NO
            )
        }

        binding.root.doOnLayout {
            it.windowInsetsControllerCompat?.isAppearanceLightStatusBars = true
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun checkAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                        this, UPDATE_CODE)
                }
                else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE,
                        this, UPDATE_CODE)
                }
            }
        }
    }

    fun getSnackInstance(string: String, duration: Int): Snackbar {
        val snackBar = Snackbar.make(binding.root, string, duration)
        snackBar.anchorView = binding.floatingActionButton
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        return snackBar
    }
}
