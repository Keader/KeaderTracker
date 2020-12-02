package dev.keader.correiostracker.view.trackdetail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding
import dev.keader.correiostracker.view.adapters.BackButtonListener
import dev.keader.correiostracker.view.adapters.DeleteItemListener
import dev.keader.correiostracker.view.adapters.TrackHistoryAdapter
import dev.keader.correiostracker.work.RefreshTracksWorker

const val TAG_VALUE_UNARCHIVED = 0
const val TAG_VALUE_ARCHIVED = 1

@AndroidEntryPoint
class TrackDetailFragment : Fragment() {

    private val trackDetailViewModel: TrackDetailViewModel by viewModels()
    private val uiViewModel: UIViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentTrackDetailBinding>(inflater,
                R.layout.fragment_track_detail, container, false)

        uiViewModel.setBottomNavVisibility(View.GONE)

        val args by navArgs<TrackDetailFragmentArgs>()
        trackDetailViewModel.setTrackCode(args.trackCode)

        binding.trackDetailViewModel = trackDetailViewModel
        binding.lifecycleOwner = this

        val adapter = TrackHistoryAdapter(DeleteItemListener { itemWithTracks ->
            trackDetailViewModel.onDeleteButtonClicked(itemWithTracks)
        }, BackButtonListener {
            findNavController().popBackStack()
        })
        binding.historyList.adapter = adapter

        trackDetailViewModel.trackItem.observe(viewLifecycleOwner, { item ->
            item?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        trackDetailViewModel.isArchived.observe(viewLifecycleOwner, { isArchived ->
            isArchived?.let {
                if (isArchived) {
                    binding.floatButton.setTag(R.id.tag_archived, TAG_VALUE_ARCHIVED)
                    binding.floatButton.setImageResource(R.drawable.ic_track_delivery)
                    binding.floatButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primaryColor))
                } else {
                    binding.floatButton.setTag(R.id.tag_archived, TAG_VALUE_UNARCHIVED)
                    binding.floatButton.setImageResource(R.drawable.ic_delivered_outline)
                    binding.floatButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                }
            }
        })

        trackDetailViewModel.eventDeleteButton.observe(viewLifecycleOwner, { eventTriggered ->
            if (eventTriggered) {
                findNavController().popBackStack()
                getSnack(getString(R.string.track_deleted))
                        ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                        ?.show()
                trackDetailViewModel.onDeleteButtonComplete()
            }
        })

        trackDetailViewModel.eventFloatButton.observe(viewLifecycleOwner, { clickedButton ->
            clickedButton?.let { isArchived ->
                if (isArchived) {
                    getSnack(getString(R.string.archived_success))
                            ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                            ?.show()
                } else {
                    getSnack(getString(R.string.unarchive_success))
                            ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                            ?.show()
                    RefreshTracksWorker.startWorker(requireNotNull(activity).application)
                }
                findNavController().popBackStack()
                trackDetailViewModel.onFloatButtonComplete()
            }
        })

        return binding.root
    }

    fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        if (activity is MainActivity)
            return activity.getSnackInstance(string, duration)
        return null
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}
