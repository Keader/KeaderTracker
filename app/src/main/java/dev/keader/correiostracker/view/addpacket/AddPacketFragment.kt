package dev.keader.correiostracker.view.addpacket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding

class AddPacketFragment : Fragment() {

    private val uiViewModel: UIViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        uiViewModel.setBottomNavVisibility(View.GONE)

        val binding = DataBindingUtil.inflate<FragmentAddPacketBinding>(inflater, R.layout.fragment_add_packet, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}