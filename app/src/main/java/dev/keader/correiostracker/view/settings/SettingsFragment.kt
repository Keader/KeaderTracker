package dev.keader.correiostracker.view.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentSettingsBinding

const val DEFAULT_SPINNER_POSITION = 3

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.frequency_array,
            android.R.layout.simple_spinner_item)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val savedPosition = sharedPref.getInt(getString(R.string.preference_frequency), DEFAULT_SPINNER_POSITION)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.setSelection(savedPosition)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (savedPosition != position) {
                    // call view model?
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        return binding.root
    }

    private fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        if (activity is MainActivity)
            return activity.getSnackInstance(string, duration)
        return null
    }
}
