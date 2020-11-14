package dev.keader.correiostracker.view.archived

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.TrackingDatabase
import dev.keader.correiostracker.databinding.FragmentArchivedBinding
import dev.keader.correiostracker.view.adapters.TrackAdapter


class ArchivedFragment : Fragment() {

    private lateinit var archivedViewModel: ArchivedViewModel
    private lateinit var binding: FragmentArchivedBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_archived, container, false)

        val application = requireNotNull(activity).application
        val db = TrackingDatabase.getInstance(application).itemDatabaseDAO
        val viewModelFactory = ArchivedViewModelFactory(db)
        archivedViewModel = ViewModelProvider(this, viewModelFactory).get(ArchivedViewModel::class.java)

        val adapter = TrackAdapter()
        binding.archivedList.adapter = adapter
        archivedViewModel.tracks.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                adapter.submitList(it)
                showRecyclerView()
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