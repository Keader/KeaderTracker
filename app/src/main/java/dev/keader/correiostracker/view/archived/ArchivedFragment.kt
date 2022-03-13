package dev.keader.correiostracker.view.archived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentArchivedBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.TrackAdapter

@AndroidEntryPoint
class ArchivedFragment : Fragment() {
    private val archivedViewModel: ArchivedViewModel by viewModels()
    private lateinit var binding: FragmentArchivedBinding
    private lateinit var trackAdapter: TrackAdapter
    private val navController
        get() = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArchivedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAdapter = TrackAdapter(archivedViewModel)
        binding.recyclerViewDelivered.adapter = trackAdapter
        setupObservers()
    }

    private fun setupObservers() {
        archivedViewModel.tracks.distinctUntilChanged().observe(viewLifecycleOwner) {
            trackAdapter.submitList(it)
            binding.recyclerViewDelivered.isVisible = it.isNotEmpty()
        }

        archivedViewModel.showEmptyTrack.distinctUntilChanged().observe(viewLifecycleOwner) {
            binding.recyclerViewEmptyDelivered.root.isVisible = it
            personalizeEmptyList()
        }

        archivedViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, EventObserver { code ->
            navController.navigate(ArchivedFragmentDirections.actionGlobalTrackDetailFragment(code))
        })
    }

    private fun personalizeEmptyList() {
        binding.recyclerViewEmptyDelivered.emptyListAnim.setAnimation(R.raw.waiting)
        binding.recyclerViewEmptyDelivered.emptyListAnim.repeatMode = LottieDrawable.REVERSE
        binding.recyclerViewEmptyDelivered.emptyListAnim.playAnimation()
        binding.recyclerViewEmptyDelivered.emptyListLabel2.text = getText(R.string.empty_list_label2)
        binding.recyclerViewEmptyDelivered.emptyListLabel3.text = getText(R.string.empty_list_sublabel2)
    }
}
