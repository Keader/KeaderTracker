package dev.keader.correiostracker.view.trackdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.database.TrackingDatabase
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding

const val TAG_ARCHIVED = 1
const val TAG_VALUE_UNARCHIVED = 0
const val TAG_VALUE_ARCHIVED = 1


class TrackDetailFragment : Fragment() {

    private lateinit var trackViewModel: TrackDetailViewModel
    private val uiViewModel: UIViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentTrackDetailBinding>(inflater,
                R.layout.fragment_track_detail, container, false)

        uiViewModel.setBottomNavVisibility(View.GONE)

        val db = TrackingDatabase.getInstance(requireContext()).itemDatabaseDAO
        val args by navArgs<TrackDetailFragmentArgs>()

        val viewModelFactory = TrackDetailViewModelFactory(db, args.trackCode)

        trackViewModel = ViewModelProvider(this, viewModelFactory).get(TrackDetailViewModel::class.java)

        trackViewModel.isArchived.observe(viewLifecycleOwner, Observer { isArchived ->
            if (isArchived) {
                binding.floatButton.setTag(TAG_ARCHIVED, TAG_VALUE_ARCHIVED)
                binding.floatButton.setImageResource(R.drawable.ic_unarchive)
            }
            else {
                binding.floatButton.setTag(TAG_ARCHIVED, TAG_VALUE_UNARCHIVED)
                binding.floatButton.setImageResource(R.drawable.ic_archived)
            }
            // When value update in DB, we can allow button work again
            trackViewModel.onFloatButtonComplete()
        })


        return binding.root
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}