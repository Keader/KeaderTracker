package dev.keader.correiostracker.view.home

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentHomeBinding
import dev.keader.correiostracker.util.EventObserver
import dev.keader.correiostracker.view.adapters.*
import dev.keader.correiostracker.view.settings.SettingsFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val adapter = TrackAdapter(ListItemListener { code ->
            homeViewModel.onItemTrackClicked(code)
        }, true)

        binding.recyclerViewDelivery.adapter = adapter
        binding.homeViewModel = homeViewModel

        val tracksDistinctLiveData = Transformations.distinctUntilChanged(homeViewModel.tracks)
        tracksDistinctLiveData.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                showEmptyList()
            } else {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val list = it.sortedBy { item ->
                    val localDate = LocalDate.parse(item.item.updatedAt, formatter)
                    return@sortedBy localDate.until(LocalDate.now(), ChronoUnit.DAYS)
                }
                adapter.addHeaderAndSubmitList(list)
                showRecyclerView()
            }
        })

        homeViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, EventObserver { code ->
            findNavController().navigate(HomeFragmentDirections.actionGlobalTrackDetailFragment(code))
        })

        homeViewModel.eventOpenInfoDialog.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.authors))
                .setMessage(getString(R.string.authors_body))
                .setPositiveButton(getString(R.string.OK)) { dialog, _ -> dialog.dismiss() }
                .show()
        })

        homeViewModel.eventOpenSettingsFragment.observe(viewLifecycleOwner, EventObserver {
            val bottomSheetFragment = SettingsFragment()
            bottomSheetFragment.show(parentFragmentManager, "Settings")
        })

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.onRefreshCalled()
        }

        homeViewModel.eventRefreshFinished.observe(viewLifecycleOwner, { finished ->
            binding.swipeRefresh.isRefreshing = !finished
        })

        // same hack to work in small screens :/
        val displayMetrics = Resources.getSystem().displayMetrics
        if (displayMetrics.widthPixels < 800 && displayMetrics.heightPixels < 1300)
            applySmallScreensHack()

        return binding.root
    }

    private fun showEmptyList() {
        binding.recyclerViewDelivery.visibility = View.GONE
        binding.recylerViewEmpty.root.visibility = View.VISIBLE
    }

    private fun showRecyclerView() {
        binding.recyclerViewDelivery.visibility = View.VISIBLE
        binding.recylerViewEmpty.root.visibility = View.GONE
    }

    private fun applySmallScreensHack() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintLayout)
        constraintSet.clear(R.id.icon_info, ConstraintSet.TOP)
        constraintSet.clear(R.id.icon_info, ConstraintSet.END)
        constraintSet.connect(R.id.icon_info, ConstraintSet.TOP, R.id.icon_config, ConstraintSet.TOP)
        constraintSet.connect(R.id.icon_info, ConstraintSet.END, R.id.icon_config, ConstraintSet.START, 16)
        constraintSet.applyTo(binding.constraintLayout)
    }

}
