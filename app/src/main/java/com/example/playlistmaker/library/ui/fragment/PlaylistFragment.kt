package com.example.playlistmaker.library.ui.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.adapter.PlaylistAdapter
import com.example.playlistmaker.library.ui.state.PlaylistState
import com.example.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter(requireContext()) { playlist ->
            if (clickDebounce()) {
                clickOnItem(playlist)
            }
        }

        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = adapter

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_NewPlaylistFragment)
        }

        viewModel.databasePlaylistState.observe(viewLifecycleOwner) { playlistState: PlaylistState ->
            render(playlistState)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    private fun clickOnItem(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_medialibraryFragment_to_playlistInfoFragment,
            PlaylistInfoFragment.createArgs(playlist)
        )
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = true

            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }

        return current
    }

    private fun View.show(isVisible: Boolean) {
        if (isVisible) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }

    private fun showLoader() {
        binding.emptyPlaylistsPlaceholder.show(false)
        binding.playlistRecyclerView.show(false)
        binding.playlistProgressbar.show(true)
    }

    private fun showPlaceholder() {
        binding.playlistRecyclerView.show(false)
        binding.playlistProgressbar.show(false)
        binding.emptyPlaylistsPlaceholder.show(true)
    }

    private fun showContent() {
        binding.playlistProgressbar.show(false)
        binding.emptyPlaylistsPlaceholder.show(false)
        binding.playlistRecyclerView.show(true)
    }

    private fun render(playlistState: PlaylistState) {

        when (playlistState) {
            is PlaylistState.Loading -> {
                showLoader()
            }

            is PlaylistState.Success -> {
                if (playlistState.data.isEmpty()) {
                    showPlaceholder()
                } else {
                    adapter?.playlists?.clear()
                    adapter?.playlists?.addAll(playlistState.data)
                    adapter?.notifyDataSetChanged()

                    showContent()
                }
            }
        }
    }

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}