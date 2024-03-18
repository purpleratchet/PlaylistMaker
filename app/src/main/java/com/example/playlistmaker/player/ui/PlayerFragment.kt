package com.example.playlistmaker.player.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.root.BottomNavigationListener
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playButton: ImageButton
    private lateinit var timer: TextView

    private var bottomNavigationListener: BottomNavigationListener? = null
    private var allowToEmit = false
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var adapter: PlaylistBottomSheetAdapter? = null
    private lateinit var playlistRecyclerView: RecyclerView
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
        super.onStop()
        hideBottomNavigation(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        initializeViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val track = arguments?.getSerializable(EXTRA_TRACK) as TrackSearchModel
        val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

        allowToEmit = false

        adapter = PlaylistBottomSheetAdapter(requireContext()) { playlist ->
            clickOnItem(playlist)
        }

        playlistRecyclerView = binding.playlistsBottomSheetRecyclerview
        playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        playlistRecyclerView.adapter = adapter

        binding.playlistsBottomSheetRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())
        binding.playlistsBottomSheetRecyclerview.adapter = adapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        preparePlayer(track)

        viewModel.observePlayerState.observe(viewLifecycleOwner) {
            playButton.isEnabled = it.isPlayButtonEnabled
            timer.text = it.progress
            playButton.setImageResource(playButtonImage(it.isPlaying))

        }

        viewModel.observeIsFavorite.observe(viewLifecycleOwner) { isFavorite ->
            val dislikeIcon =
                if (isDarkTheme()) R.drawable.ic_dislike_dark else R.drawable.ic_dislike
            val favoriteIcon =
                if (isDarkTheme()) R.drawable.ic_favorite_dark else R.drawable.ic_favorite
            binding.btnFavorite.setImageResource(if (isFavorite) dislikeIcon else favoriteIcon)
        }

        binding.btnPlayerBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnFavorite.setOnClickListener { viewModel.onFavoriteClicked() }
        playButton.setOnClickListener { viewModel.playbackControl() }

        viewModel.playlistsFromDatabase.observe(viewLifecycleOwner) { listOfPlaylists ->
            addPlaylistsToBottomSheetRecyclerView(listOfPlaylists)
        }

        viewModel.checkIsTrackInPlaylist.observe(viewLifecycleOwner) { playlistTrackState ->
            if (allowToEmit) {
                if (playlistTrackState.trackIsInPlaylist) {
                    val toastPhrase =
                        "Трек уже добавлен в плейлист" + " ${playlistTrackState.nameOfPlaylist}"
                    Toast.makeText(requireContext(), toastPhrase, Toast.LENGTH_SHORT).show()
                } else {
                    val toastPhrase =
                        "Добавлено в плейлист" + " ${playlistTrackState.nameOfPlaylist}"
                    Toast.makeText(requireContext(), toastPhrase, Toast.LENGTH_SHORT).show()
                    viewModel.getPlaylists()
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
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

        binding.btnAdd.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.getPlaylists()
        }

        binding.createPlaylistBottomSheetButton.setOnClickListener {
            viewModel.releaseAudioPlayer()
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun initializeViews() {
        playButton = binding.btnPlay
        timer = binding.progressTime
    }
    private fun playButtonImage(isPlaying: Boolean): Int {
        val playIcon = if (isDarkTheme()) R.drawable.ic_play_dark else R.drawable.ic_play
        val pauseIcon = if (isDarkTheme()) R.drawable.ic_pause_dark else R.drawable.ic_pause
        return if (isPlaying) pauseIcon else playIcon
    }
    private fun isDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
    private fun preparePlayer(track: TrackSearchModel) {
        binding.apply {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTimeResult.text = track.trackTimeMillis
            collectionName.text = track.collectionName
            releaseDate.text = track.releaseDate.substring(0, 4)
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country
            progressTime.text = getString(R.string.default_playtime_value)
        }
        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(binding.trackCover)
    }

    fun hideBottomNavigation(isHide: Boolean) {
        bottomNavigationListener?.toggleBottomNavigationViewVisibility(!isHide)
    }

    fun addPlaylistsToBottomSheetRecyclerView(listOfPlaylists: List<Playlist>) {
        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(listOfPlaylists)
        adapter?.notifyDataSetChanged()
    }

    private fun clickOnItem(playlist: Playlist) {
        val track = arguments?.getSerializable(EXTRA_TRACK) as TrackSearchModel
        val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

        allowToEmit = true
        viewModel.checkAndAddTrackToPlaylist(playlist, track)

    }
    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"

        fun newInstance(track: TrackSearchModel): PlayerFragment {
            val args = Bundle().apply {
                putSerializable(EXTRA_TRACK, track)
            }
            val fragment = PlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}