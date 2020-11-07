package dev.keader.correiostracker.trackdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding

class TrackDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentTrackDetailBinding>(inflater, R.layout.fragment_track_detail, container, false)
        return binding.root
    }

}