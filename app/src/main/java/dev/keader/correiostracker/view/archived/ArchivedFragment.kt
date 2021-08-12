package dev.keader.correiostracker.view.archived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.databinding.FragmentArchivedBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.distinctUntilChanged
import dev.keader.correiostracker.view.adapters.ListItemListener
import dev.keader.correiostracker.view.adapters.TrackAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class ArchivedFragment : Fragment() {
    private val archivedViewModel: ArchivedViewModel by viewModels()
    private lateinit var binding: FragmentArchivedBinding
    private val navController
        get() = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentArchivedBinding.inflate(inflater, container, false)

        val adapter = TrackAdapter(ListItemListener { code ->
            archivedViewModel.onItemTrackClicked(code)
        })

        binding.recyclerViewDelivered.adapter = adapter
        archivedViewModel.tracks.distinctUntilChanged().observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val list = it.sortedBy { item ->
                    val localDate = LocalDate.parse(item.item.updatedAt, formatter)
                    return@sortedBy localDate.until(LocalDate.now(), ChronoUnit.DAYS)
                }
                adapter.submitList(list)
                showRecyclerView()
            }
        })

        archivedViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, EventObserver { code ->
            navController.navigate(ArchivedFragmentDirections.actionGlobalTrackDetailFragment(code))
        })

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun showEmptyList() {
        binding.recyclerViewEmptyDelivered.root.visibility = View.VISIBLE
        binding.recyclerViewDelivered.visibility = View.GONE
    }

    private fun showRecyclerView() {
        binding.recyclerViewEmptyDelivered.root.visibility = View.GONE
        binding.recyclerViewDelivered.visibility = View.VISIBLE
    }
}
