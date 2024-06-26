package com.example.playlistmaker.library.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.ui.state.FavoritesState
import com.example.playlistmaker.library.ui.viewmodel.FavoriteViewModel
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.ui.TracksAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel.Companion.EXTRA_TRACK
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoriteViewModel by viewModel()
    private lateinit var rvFavorites: RecyclerView
    private lateinit var onTrackClick: (TrackSearchModel) -> Unit
    private val favoritesAdapter = TracksAdapter {
        onTrackClick(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        rvFavorites = binding.rvFavorites
        rvFavorites.adapter = favoritesAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClick = {
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                bundleOf(EXTRA_TRACK to it)
            )
        }

        viewModel.observeFavoritesState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun render(state: FavoritesState) {
        rvFavorites.visibility = when (state) {
            is FavoritesState.Loading -> View.GONE
            is FavoritesState.Empty -> {
                favoritesAdapter.tracks.clear()
                favoritesAdapter.notifyDataSetChanged()
                binding.placeHolderText.text = state.message
                View.GONE
            }

            is FavoritesState.Content -> {
                favoritesAdapter.tracks.clear()
                favoritesAdapter.tracks.addAll(state.favorites)
                favoritesAdapter.notifyDataSetChanged()
                View.VISIBLE
            }
        }

        binding.favoritesPlaceholder.visibility = when (state) {
            is FavoritesState.Loading -> View.GONE
            is FavoritesState.Empty -> View.VISIBLE
            is FavoritesState.Content -> View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}