package dev.keader.correiostracker.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentHomeBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.ListItemListener
import dev.keader.correiostracker.view.adapters.TrackAdapter
import dev.keader.correiostracker.view.adapters.TrackHeaderAdapter
import dev.keader.correiostracker.view.dontkill.DontKillFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val navController
        get() = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val headerAdapter = TrackHeaderAdapter()

        val trackAdapter = TrackAdapter(ListItemListener { code ->
            homeViewModel.onItemTrackClicked(code)
        })
        val concatAdapter = ConcatAdapter(headerAdapter, trackAdapter)

        binding.recyclerViewDelivery.adapter = concatAdapter

        homeViewModel.tracks.distinctUntilChanged().observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                val list = it.sortedBy { item ->
                    try { parserDate(item.item.updatedAt) }
                    catch (ex: Exception) { parserDate(item.item.updatedAt, false) }
                }
                trackAdapter.submitList(list)
                showRecyclerView()
            }
        })

        homeViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, EventObserver { code ->
            navController.navigate(HomeFragmentDirections.actionGlobalTrackDetailFragment(code))
        })

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.onRefreshCalled()
        }

        homeViewModel.eventRefreshRunning.observe(viewLifecycleOwner, { running ->
            binding.swipeRefresh.isRefreshing = running
        })

        if (homeViewModel.shouldShowDontKillAlert()) {
            homeViewModel.saveDontKillAlert()
            DontKillFragment().show(parentFragmentManager, "DontKill")
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun parserDate(dateTime: String, withSeconds: Boolean = true): Long {
        val formatter = if (withSeconds)
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        else
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val localDateTime = LocalDateTime.parse(dateTime, formatter)
        return localDateTime.until(LocalDateTime.now(), ChronoUnit.DAYS)
    }

    private fun showEmptyList() {
        binding.recyclerViewDelivery.visibility = View.GONE
        binding.recylerViewEmpty.root.visibility = View.VISIBLE
    }

    private fun showRecyclerView() {
        binding.recyclerViewDelivery.visibility = View.VISIBLE
        binding.recylerViewEmpty.root.visibility = View.GONE
    }
}
