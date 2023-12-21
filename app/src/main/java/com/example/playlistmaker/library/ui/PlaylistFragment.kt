package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
    }

    private fun setupUI() {
        binding.ivNoPlaylist.setImageResource(R.drawable.ic_no_results)
        binding.tvNoPlaylist.text = getString(R.string.no_playlists)
    }

    private fun observeLiveData() {
        viewModel.liveData.observe(viewLifecycleOwner) {
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}