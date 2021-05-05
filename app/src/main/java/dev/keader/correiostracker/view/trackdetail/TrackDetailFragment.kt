package dev.keader.correiostracker.view.trackdetail

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Build
import android.os.Build.HARDWARE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.BuildConfig
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding
import dev.keader.correiostracker.util.toFile
import dev.keader.correiostracker.view.adapters.*
import dev.keader.correiostracker.work.RefreshTracksWorker

const val TAG_VALUE_UNARCHIVED = 0
const val TAG_VALUE_ARCHIVED = 1

@AndroidEntryPoint
class TrackDetailFragment : Fragment() {

    private val trackDetailViewModel: TrackDetailViewModel by viewModels()
    private val uiViewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentTrackDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_track_detail,
            container,false)

        uiViewModel.setBottomNavVisibility(View.GONE)

        val args by navArgs<TrackDetailFragmentArgs>()
        trackDetailViewModel.setTrackCode(args.trackCode)

        binding.trackDetailViewModel = trackDetailViewModel
        binding.lifecycleOwner = this

        val adapter = TrackHistoryAdapter(TrackHistoryButtonListener { itemWithTracks, buttonId ->
            when (buttonId) {
                BUTTON_BACK -> findNavController().popBackStack()
                BUTTON_COPY -> copyTrackCodeAndShowSnack(itemWithTracks)
                BUTTON_DELETE -> trackDetailViewModel.onDeleteButtonClicked(itemWithTracks)
                BUTTON_SHARE -> handleWithShare()
            }
        })

        binding.recyclerViewHistory.adapter = adapter

        trackDetailViewModel.trackItem.observe(viewLifecycleOwner, { item ->
            item?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        trackDetailViewModel.isArchived.observe(viewLifecycleOwner, { isArchived ->
            isArchived?.let {
                if (isArchived) {
                    binding.floatButtonArchive.setTag(R.id.tag_archived, TAG_VALUE_ARCHIVED)
                    binding.floatButtonArchive.setImageResource(R.drawable.ic_track_delivery)
                    binding.floatButtonArchive.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primaryColor
                        )
                    )
                } else {
                    binding.floatButtonArchive.setTag(R.id.tag_archived, TAG_VALUE_UNARCHIVED)
                    binding.floatButtonArchive.setImageResource(R.drawable.ic_delivered_outline)
                    binding.floatButtonArchive.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.secondaryColor
                        ))
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

    private fun handleWithShare() {
        val context = requireContext()
        val file = requireView().drawToBitmap().toFile(context)
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/jpg"
        context.startActivity(intent)
    }

    private fun copyTrackCodeAndShowSnack(itemWithTracks: ItemWithTracks) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Product Code", itemWithTracks.item.code)
        clipboard.setPrimaryClip(clip)
        getSnack(getString(R.string.copy_success))
            ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
            ?.show()
    }

    private fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
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
