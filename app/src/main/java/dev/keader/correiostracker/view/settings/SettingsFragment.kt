package dev.keader.correiostracker.view.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentSettingsBinding
import dev.keader.correiostracker.util.EventObserver
import dev.keader.correiostracker.work.RefreshTracksWorker

const val DEFAULT_SPINNER_POSITION = 3
const val DEFAULT_FREQUENCY_VALUE = 120
const val DEFAULT_THEME_VALUE = false
const val DEFAULT_AUTOMOVE = false

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.settingsViewModel = settingsViewModel

        // Shared Prefs
        val sharedPref = requireActivity().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE)
        var savedPosition = sharedPref.getInt(getString(R.string.preference_frequency_pos), DEFAULT_SPINNER_POSITION)
        binding.switchAutosave.isChecked = sharedPref.getBoolean(getString(R.string.preference_automove), DEFAULT_AUTOMOVE)
        binding.switchTheme.isChecked = sharedPref.getBoolean(getString(R.string.preference_theme), DEFAULT_THEME_VALUE)

        // Configure Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.frequency_array,
                android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequency.adapter = spinnerAdapter
        binding.spinnerFrequency.setSelection(savedPosition)

        // Events
        settingsViewModel.eventNavigateBack.observe(viewLifecycleOwner, EventObserver {
            dismiss()
        })

        settingsViewModel.eventNavigateOK.observe(viewLifecycleOwner, EventObserver {
            val sharedEdit = sharedPref.edit()
            val autoMove = binding.switchAutosave.isChecked
            val position = binding.spinnerFrequency.selectedItemPosition
            val darkTheme = binding.switchTheme.isChecked
            val frequency = when (position) {
                0 -> 15
                1 -> 30
                2 -> 60
                3 -> 120
                4 -> 240
                5 -> 480
                else -> DEFAULT_FREQUENCY_VALUE
            }
            // Update shared prefs
            sharedEdit.putBoolean(getString(R.string.preference_automove), autoMove)
            sharedEdit.putInt(getString(R.string.preference_frequency), frequency)
            sharedEdit.putInt(getString(R.string.preference_frequency_pos), position)
            sharedEdit.putBoolean(getString(R.string.preference_theme), darkTheme)
            sharedEdit.commit()
            // Replace current worker
            RefreshTracksWorker.startWorker(requireNotNull(activity).application)

            if (autoMove)
                settingsViewModel.handleArchiveAllCurrentItems()

            if (darkTheme)
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

            getSnack(getString(R.string.settings_success))
                ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                ?.show()
            dismiss()
        })

        return binding.root
    }

    private fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        if (activity is MainActivity)
            return activity.getSnackInstance(string, duration)
        return null
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}
