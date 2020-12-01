package dev.keader.correiostracker.view.archived

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentArchivedBinding
import dev.keader.correiostracker.view.adapters.TrackAdapter
import dev.keader.correiostracker.view.adapters.ListItemListener
import java.text.SimpleDateFormat

@AndroidEntryPoint
class ArchivedFragment : Fragment() {

    private val archivedViewModel: ArchivedViewModel by viewModels()
    private lateinit var binding: FragmentArchivedBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_archived, container, false)

        val adapter = TrackAdapter( ListItemListener { code ->
            archivedViewModel.onItemTrackClicked(code)
        })
        binding.archivedList.adapter = adapter
        archivedViewModel.tracks.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                val dates = SimpleDateFormat("dd/mm/yyyy")
                val list = it.sortedBy { item ->dates.parse(item.tracks.first().date)?.time }
                adapter.submitList(list)
                showRecyclerView()
            }
        })

        archivedViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, { code ->
            code?.let{
                findNavController().navigate(ArchivedFragmentDirections.actionGlobalTrackDetailFragment(code))
                archivedViewModel.handleNavigateToTrackDetailFinish()
            }
        })

        return binding.root
    }

    private fun showEmptyList() {
        binding.emptyListArchived.root.visibility = View.VISIBLE
        binding.archivedList.visibility = View.GONE
    }

    private fun showRecyclerView() {
        binding.emptyListArchived.root.visibility = View.GONE
        binding.archivedList.visibility = View.VISIBLE
    }
}
