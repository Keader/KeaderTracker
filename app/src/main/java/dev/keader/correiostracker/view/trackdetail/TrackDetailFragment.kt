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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.database.TrackingDatabase
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding
import dev.keader.correiostracker.view.adapters.TrackHistoryAdapter

const val TAG_VALUE_UNARCHIVED = 0
const val TAG_VALUE_ARCHIVED = 1


class TrackDetailFragment : Fragment() {

    private lateinit var trackDetailViewModel: TrackDetailViewModel
    private val uiViewModel: UIViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentTrackDetailBinding>(inflater,
                R.layout.fragment_track_detail, container, false)

        uiViewModel.setBottomNavVisibility(View.GONE)

        val application = requireNotNull(activity).application

        val db = TrackingDatabase.getInstance(application).itemDatabaseDAO
        val args by navArgs<TrackDetailFragmentArgs>()

        val viewModelFactory = TrackDetailViewModelFactory(db, args.trackCode)

        trackDetailViewModel = ViewModelProvider(this, viewModelFactory).get(TrackDetailViewModel::class.java)

        binding.trackDetailViewModel = trackDetailViewModel
        binding.lifecycleOwner = this


        val adapter = TrackHistoryAdapter()
        binding.historyList.adapter = adapter

        trackDetailViewModel.trackItem.observe(viewLifecycleOwner, { item ->
            adapter.addHeaderAndSubmitList(item)
        })

        trackDetailViewModel.isArchived.observe(viewLifecycleOwner, { isArchived ->
            if (isArchived) {
                binding.floatButton.setTag(R.id.tag_archived, TAG_VALUE_ARCHIVED)
                binding.floatButton.setImageResource(R.drawable.ic_unarchive)
                binding.floatButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            }
            else {
                binding.floatButton.setTag(R.id.tag_archived, TAG_VALUE_UNARCHIVED)
                binding.floatButton.setImageResource(R.drawable.ic_archived)
                binding.floatButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
            }
        })

        trackDetailViewModel.eventFloatButton.observe(viewLifecycleOwner, { eventTrigger ->
            if (eventTrigger) {
                findNavController().popBackStack()
                trackDetailViewModel.onFloatButtonComplete()
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}