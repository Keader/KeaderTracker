package dev.keader.correiostracker.view.archived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentArchivedBinding
import dev.keader.correiostracker.util.EventObserver
import dev.keader.correiostracker.view.adapters.ListItemListener
import dev.keader.correiostracker.view.adapters.TrackAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class ArchivedFragment : Fragment() {
    private val uiViewModel: UIViewModel by activityViewModels()
    private val archivedViewModel: ArchivedViewModel by viewModels()
    private lateinit var binding: FragmentArchivedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_archived, container, false)

        val adapter = TrackAdapter(ListItemListener { code ->
            archivedViewModel.onItemTrackClicked(code)
        })

        binding.recyclerViewDelivered.adapter = adapter
        val tracksDistinctLiveData = Transformations.distinctUntilChanged(archivedViewModel.tracks)
        tracksDistinctLiveData.observe(viewLifecycleOwner, {
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
            findNavController().navigate(ArchivedFragmentDirections.actionGlobalTrackDetailFragment(code))
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

    override fun onResume() {
        super.onResume()
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
    }
}
