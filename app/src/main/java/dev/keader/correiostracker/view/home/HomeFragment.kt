package dev.keader.correiostracker.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.TrackingDatabase
import dev.keader.correiostracker.databinding.FragmentHomeBinding
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.view.adapters.TrackAdapter
import dev.keader.correiostracker.view.adapters.ListItemListener


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val application = requireNotNull(activity).application
        val db = TrackingDatabase.getInstance(application).itemDatabaseDAO
        val repository = TrackingRepository(db)
        val viewModelFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        val adapter = TrackAdapter(ListItemListener { code ->
            homeViewModel.onItemTrackClicked(code)
        })

        binding.deliveryList.adapter = adapter

        homeViewModel.tracks.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                adapter.submitList(it)
                showRecyclerView()
            }
        })

        homeViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, Observer { code ->
            code?.let{
                findNavController().navigate(HomeFragmentDirections.actionGlobalTrackDetailFragment(code))
                homeViewModel.handleNavigateToTrackDetailFinish()
            }
        })

        return binding.root
    }

    private fun showEmptyList() {
        binding.deliveryAnim.visibility = View.GONE
        binding.deliveryList.visibility = View.GONE
        binding.deliveryLabel.visibility = View.GONE
        binding.emptyList.root.visibility = View.VISIBLE
    }

    private fun showRecyclerView() {
        binding.deliveryAnim.visibility = View.VISIBLE
        binding.deliveryList.visibility = View.VISIBLE
        binding.deliveryLabel.visibility = View.VISIBLE
        binding.emptyList.root.visibility = View.GONE
    }

}
