package dev.keader.correiostracker.view.trackdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.BuildConfig
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.DialogEditItemNameBinding
import dev.keader.correiostracker.databinding.FragmentTrackDetailBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.model.toFile
import dev.keader.correiostracker.view.adapters.TrackHistoryAdapter
import dev.keader.correiostracker.work.RefreshTracksWorker
import dev.keader.sharedapiobjects.ItemWithTracks
import javax.inject.Inject

@AndroidEntryPoint
class TrackDetailFragment : Fragment() {

    private val trackDetailViewModel: TrackDetailViewModel by viewModels()
    private val uiViewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentTrackDetailBinding
    private lateinit var trackHistoryAdapter: TrackHistoryAdapter
    private val navController
        get() = findNavController()
    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTrackDetailBinding.inflate(inflater, container,false)
        binding.trackDetailViewModel = trackDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiViewModel.setBottomNavVisibility(false)
        val args by navArgs<TrackDetailFragmentArgs>()
        trackDetailViewModel.setTrackCode(args.trackCode)
        trackHistoryAdapter = TrackHistoryAdapter(trackDetailViewModel)
        binding.recyclerViewHistory.adapter = trackHistoryAdapter
        setupObservers()
    }

    private fun setupObservers() {
        trackDetailViewModel.trackItem.observe(viewLifecycleOwner) { item ->
            item?.let { trackHistoryAdapter.addHeaderAndSubmitList(it) }
        }

        trackDetailViewModel.isArchived.observe(viewLifecycleOwner) { fabStatus ->
            binding.floatButtonArchive.setTag(R.id.tag_archived, fabStatus.tag)
            binding.floatButtonArchive.setImageResource(fabStatus.imgSrc)
            binding.floatButtonArchive.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), fabStatus.bgColor)
            )
        }

        trackDetailViewModel.eventNavigateAfterDelete.observe(viewLifecycleOwner, EventObserver {
            navController.popBackStack()
            getSnack(getString(R.string.track_deleted))
                ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                ?.show()
        })

        trackDetailViewModel.eventItemArchived.observe(viewLifecycleOwner, EventObserver {
            getSnack(getString(R.string.archived_success))
                ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                ?.show()
            navController.popBackStack()
        })

        trackDetailViewModel.eventItemUnArchived.observe(viewLifecycleOwner, EventObserver {
            getSnack(getString(R.string.unarchive_success))
                ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                ?.show()
            RefreshTracksWorker.startWorker(requireActivity().application, preferences)
            navController.popBackStack()
        })

        trackDetailViewModel.eventNavigateBack.observe(viewLifecycleOwner, EventObserver {
            navController.popBackStack()
        })

        trackDetailViewModel.eventCopyButton.observe(viewLifecycleOwner, EventObserver {
            copyTrackCodeAndShowSnack(it)
        })

        trackDetailViewModel.eventDeleteButton.observe(viewLifecycleOwner, EventObserver {
            onDeleteButtonClicked(it)
        })

        trackDetailViewModel.eventShareButton.observe(viewLifecycleOwner, EventObserver {
            onShareButtonClicked()
        })

        trackDetailViewModel.eventEditButton.observe(viewLifecycleOwner, EventObserver {
            onEditButtonClicked()
        })

        trackDetailViewModel.eventRefreshRunning.observe(viewLifecycleOwner) { running ->
            binding.swipeRefresh.isRefreshing = running
        }
    }

    private fun onDeleteButtonClicked(itemWithTracks: ItemWithTracks) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_title))
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                trackDetailViewModel.handleDeleteItem(itemWithTracks)
                dialog.dismiss()
            }
            .show()
    }

    private fun copyTrackCodeAndShowSnack(itemWithTracks: ItemWithTracks) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Product Code", itemWithTracks.item.code)
        clipboard.setPrimaryClip(clip)
        getSnack(getString(R.string.copy_success))
            ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
            ?.show()
    }

    private fun onShareButtonClicked() {
        val file = requireView().drawToBitmap().toFile(requireContext())
        val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/jpg"
        requireContext().startActivity(intent)
    }

    private fun onEditButtonClicked() {
        val binding = DialogEditItemNameBinding.inflate(requireActivity().layoutInflater)
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit_package_description))
            .setView(binding.root)
            .setPositiveButton(getString(R.string.OK)) { dialog, _ ->
                binding.inputText.text?.let { newName ->
                    trackDetailViewModel.handleWithUpdateName(newName.toString())
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_2)) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val myActivity = activity as? MainActivity
        return myActivity?.getSnackInstance(string, duration)
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(true)
        super.onDestroyView()
    }
}
