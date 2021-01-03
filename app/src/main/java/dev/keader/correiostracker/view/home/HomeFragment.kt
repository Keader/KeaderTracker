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
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.FragmentHomeBinding
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

        binding.deliveryList.adapter = adapter
        binding.homeViewModel = homeViewModel

        homeViewModel.tracks.observe(viewLifecycleOwner, {
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

        homeViewModel.eventNavigateToTrackDetail.observe(viewLifecycleOwner, { code ->
            code?.let {
                findNavController().navigate(HomeFragmentDirections.actionGlobalTrackDetailFragment(code))
                homeViewModel.handleNavigateToTrackDetailFinish()
            }
        })

        homeViewModel.eventOpenInfoDialog.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.authors))
                    .setMessage(getString(R.string.authors_body))
                    .setPositiveButton(getString(R.string.OK)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                homeViewModel.onInfoButtonEventFinished()
            }
        })

        homeViewModel.eventOpenSettingsFragment.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                val bottomSheetFragment = SettingsFragment()
                bottomSheetFragment.show(parentFragmentManager, "Settings")
                homeViewModel.onSettingsButtonEventFinished()
            }
        })

        // same hack to works in small screen :/
        val displayMetrics = Resources.getSystem().displayMetrics
        if (displayMetrics.widthPixels < 800 && displayMetrics.heightPixels < 1300) {
            R.id.localization_icon
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.constraintLayout)
            constraintSet.clear(R.id.icon_info, ConstraintSet.TOP)
            constraintSet.clear(R.id.icon_info, ConstraintSet.END)
            constraintSet.connect(R.id.icon_info, ConstraintSet.TOP, R.id.icon_config, ConstraintSet.TOP)
            constraintSet.connect(R.id.icon_info, ConstraintSet.END, R.id.icon_config, ConstraintSet.START, 16)
            constraintSet.applyTo(binding.constraintLayout)
        }

        return binding.root
    }

    private fun showEmptyList() {
        binding.deliveryList.visibility = View.GONE
        binding.emptyList.root.visibility = View.VISIBLE
    }

    private fun showRecyclerView() {
        binding.deliveryList.visibility = View.VISIBLE
        binding.emptyList.root.visibility = View.GONE
    }

}
