package dev.keader.correiostracker.view.dontkill

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentDontKillBinding
import dev.keader.correiostracker.model.PreferencesManager
import javax.inject.Inject

@AndroidEntryPoint
class DontKillFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDontKillBinding
    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDontKillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            handleConfirmButton()
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun handleConfirmButton() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dontkillmyapp.com/"))
        requireContext().startActivity(intent)
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}
