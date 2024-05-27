package com.example.playlistmaker.library.ui.fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.library.domain.container.PlaylistInfoContainer
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.viewmodel.PlaylistInfoViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.root.BottomNavigationListener
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.ui.TracksAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.Serializable

class PlaylistInfoFragment : Fragment() {
    var isClickAllowed = true
    private var bottomNavigationListener: BottomNavigationListener? = null
    private lateinit var binding: FragmentPlaylistInfoBinding
    var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    var playlist: Playlist? = null
    private val onLongClickAction = fun(track: TrackSearchModel): Boolean {
        showDeleteTrackDialog(track)
        return true
    }
    val adapter = TracksAdapter(onLongClickAction, true) { track ->
        onClickItem(track)
    }
    private val viewModel: PlaylistInfoViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        } else {
            throw IllegalArgumentException("Activity must implement BottomNavigationListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(true)
    }
    override fun onStop() {
        isClickAllowed = true
        super.onStop()
        hideBottomNavigation(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistInfoBottomSheetRecyclerview.adapter = adapter
        binding.playlistInfoBottomSheetRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMenuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        // Get Playlist
        if (viewModel.updatedPlaylist == null) {
            playlist =
                requireArguments().getSerializableExtra(CURRENT_PLAYLIST, Playlist::class.java)
        } else {
            playlist = viewModel.updatedPlaylist
        }
        if (playlist!!.listOfTracksId.isEmpty()) {
            Toast.makeText(requireContext(), "В плейлисте пока нет треков", Toast.LENGTH_SHORT)
                .show()
        }

        renderWithSerializableData()
        viewModel.getTracksFromDatabaseForCurrentPlaylist(viewModel.convertStringToList(playlist!!.listOfTracksId))

        binding.playlistInfoBackarrowImageview.setOnClickListener {
            findNavController().popBackStack(R.id.libraryFragment, false)
        }

        binding.playlistInfoShareImageview.setOnClickListener {
            if (clickDebounce()) {
                sharePlaylist()
            }
        }

        binding.playlistInfoMenuImageview.setOnClickListener {
            if (clickDebounce()) {
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.sharePlaylistFramelayout.setOnClickListener {
            sharePlaylist()
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.editPlaylistFramelayout.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlist!!)
            )
        }

        binding.deletePlaylistFramelayout.setOnClickListener {
            if (playlist != null) {
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                showDeletePlaylistDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.libraryFragment, false)
                }
            })

        viewModel.tracksForCurrentPlaylist.observe(viewLifecycleOwner) { playlistInfoContainer ->
            renderWithDatabaseData(playlistInfoContainer)
        }
    }

    private fun sharePlaylist() {
        if (playlist != null) {
            if (playlist!!.listOfTracksId.isEmpty()) {
                showShareEmptyPlaylistDialog()
            } else {
                sendMessageToExternalResources()
            }
        }
    }

    private fun showShareEmptyPlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MyDialogTheme)
            .setMessage(getString(R.string.share_empty_playlist))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
            }
            .show()
    }

    private fun sendMessageToExternalResources() {
        try {
            val message = viewModel.getMessageForExternalResources(playlist)
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context, getString(R.string.share_app_not_found),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showDeleteTrackDialog(track: TrackSearchModel) {
        MaterialAlertDialogBuilder(requireContext(), R.style.MyDialogTheme)
            .setTitle(getString(R.string.delete_track_title))
            .setMessage(getString(R.string.delete_track_message))
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                deleteTrackFromPlaylist(track)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MyDialogTheme)
            .setTitle(getString(R.string.delete_playlist_title))
            .setMessage(getString(R.string.delete_playlist_ask))
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewModel.deletePlaylist(playlist)
                findNavController().popBackStack(R.id.libraryFragment, false)
            }
            .show()
    }

    private fun deleteTrackFromPlaylist(track: TrackSearchModel) {
        val listStringOfTrackIds: String? = playlist?.listOfTracksId
        val listOfTrackIds = listStringOfTrackIds?.let { viewModel.convertStringToList(it) }
        listOfTrackIds?.remove(track.trackId.toInt())
        val newListStringOfTrackIds =
            listOfTrackIds?.let { viewModel.convertListToString(listOfTrackIds) }
        val updatedPlaylist = newListStringOfTrackIds?.let {
            playlist?.copy(
                listOfTracksId = it,
                amountOfTracks = playlist!!.amountOfTracks - 1
            )
        }
        playlist = updatedPlaylist
        viewModel.updatedPlaylist = updatedPlaylist
        if (updatedPlaylist != null) {
            viewModel.updatePlaylist(updatedPlaylist)
        }
        viewModel.checkAndDeleteTrackFromPlaylistTrackDatabase(track)
        if (listOfTrackIds != null) {
            viewModel.getTracksFromDatabaseForCurrentPlaylist(listOfTrackIds)
        }
        binding.amountOfTracksPlaylistInfoTextview.text =
            playlist?.let { viewModel.pluralizeWord(it.amountOfTracks, "трек") }
    }

    private fun onClickItem(track: TrackSearchModel) {
        findNavController().navigate(
            R.id.action_playlistInfoFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    private fun renderWithSerializableData() {
        if (playlist != null) {
            if (playlist!!.filePath.isNotEmpty()) {
                binding.playlistInfoCoverImageview.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.playlistInfoCoverImageview.setImageURI(getUriOfImageFromStorage(playlist!!.filePath))
                binding.playlistInfoCoverMinImageview.setImageURI(getUriOfImageFromStorage(playlist!!.filePath))
            } else {
                binding.playlistInfoCoverImageview.setImageResource(R.drawable.placeholder_max)
                binding.playlistInfoCoverMinImageview.setImageResource(R.drawable.placeholder)
            }
            binding.nameOfPlaylistInfoTextview.text = playlist!!.name
            binding.yearOfPlaylistInfoTextview.text = playlist!!.description
            binding.amountOfTracksPlaylistInfoTextview.text =
                viewModel.pluralizeWord(playlist!!.amountOfTracks, "трек")
            binding.nameOfPlaylistInfoMinTextview.text = playlist!!.name
            binding.amountOfTracksPlaylistInfoMinTextview.text =
                viewModel.pluralizeWord(playlist!!.amountOfTracks, "трек")
        }
    }

    private fun renderWithDatabaseData(playlistInfoContainer: PlaylistInfoContainer) {
        viewModel.listOfCurrentTracks.clear()
        viewModel.listOfCurrentTracks.addAll(playlistInfoContainer.playlistTracks)
        adapter.tracks.clear()
        adapter.tracks.addAll(playlistInfoContainer.playlistTracks)
        adapter.notifyDataSetChanged()
        binding.totalMinutesPlaylistInfoTextview.text = playlistInfoContainer.totalTime
    }

    private fun <T : Serializable?> Bundle.getSerializableExtra(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializable(key, m_class)!!
        else
            this.getSerializable(key) as T
    }
    private fun getUriOfImageFromStorage(fileName: String): Uri {
        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, fileName)
        return file.toUri()
    }
    private fun hideBottomNavigation(isHide: Boolean) {
        bottomNavigationListener?.toggleBottomNavigationViewVisibility(!isHide)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CURRENT_PLAYLIST = "CURRENT_PLAYLIST"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun createArgs(playlist: Playlist): Bundle {
            return bundleOf(CURRENT_PLAYLIST to playlist)
        }
    }
}