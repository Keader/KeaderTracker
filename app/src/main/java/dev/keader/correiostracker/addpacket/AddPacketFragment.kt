package dev.keader.correiostracker.addpacket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding

class AddPacketFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentAddPacketBinding>(inflater, R.layout.fragment_add_packet, container, false)
        return binding.root
    }
}