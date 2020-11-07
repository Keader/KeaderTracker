package dev.keader.correiostracker.trackdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding
import timber.log.Timber

class TrackDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentTrackDetailBinding>(inflater, R.layout.fragment_track_detail, container, false)

        // Recuperando args
        val args by navArgs<TrackDetailFragmentArgs>()

        Timber.i("Got code: ${args.trackCode}")

        return binding.root
    }

}