package dev.keader.correiostracker.view.trackdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding
import timber.log.Timber

class TrackDetailFragment : Fragment() {

    private val viewModel: TrackDetailViewModel by viewModels()
    private val uiViewModel: UIViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentTrackDetailBinding>(inflater, R.layout.fragment_track_detail, container, false)

        uiViewModel.setBottomNavVisibility(View.GONE)
        // Recuperando args
        val args by navArgs<TrackDetailFragmentArgs>()

        Timber.i("Got code: ${args.trackCode}")

        return binding.root
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}