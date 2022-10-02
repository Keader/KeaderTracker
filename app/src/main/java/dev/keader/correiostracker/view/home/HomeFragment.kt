package dev.keader.correiostracker.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentHomeBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.TrackAdapter
import dev.keader.correiostracker.view.adapters.TrackHeaderAdapter
import dev.keader.correiostracker.view.dontkill.DontKillFragment

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var trackAdapter: TrackAdapter
    private val navController
        get() = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAdapter = TrackAdapter(homeViewModel)
        val headerAdapter = TrackHeaderAdapter()
        val concatAdapter = ConcatAdapter(headerAdapter, trackAdapter)
        binding.recyclerViewDelivery.adapter = concatAdapter
        homeViewModel.checkDontKillAlert()
        setupObservers()
    }

    private fun setupObservers() {
        homeViewModel.tracks.distinctUntilChanged().observe(viewLifecycleOwner) {
            trackAdapter.submitList(it)
            binding.recyclerViewDelivery.isVisible = it.isNotEmpty()
        }

        homeViewModel.showEmptyTrack.distinctUntilChanged().observe(viewLifecycleOwner) {
            binding.recylerViewEmpty.root.isVisible = it
        }

        homeViewModel.eventNavigateToTrackDetail.observe(
            viewLifecycleOwner,
            EventObserver { code ->
                navController.navigate(HomeFragmentDirections.actionGlobalTrackDetailFragment(code))
            }
        )

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.onRefreshCalled()
        }

        homeViewModel.eventRefreshRunning.observe(viewLifecycleOwner) { running ->
            binding.swipeRefresh.isRefreshing = running
        }

        homeViewModel.eventShowDontKillAlert.observe(
            viewLifecycleOwner,
            EventObserver {
                DontKillFragment().show(parentFragmentManager, "DontKill")
            }
        )
    }
}
