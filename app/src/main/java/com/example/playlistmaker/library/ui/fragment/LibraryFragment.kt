package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.library.ui.LibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private var _tabMediator: TabLayoutMediator? = null
    private val tabMediator get() = _tabMediator!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle)

        _tabMediator =
            TabLayoutMediator(binding.libraryTabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorites_tracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabMediator.attach()
    }

    override fun onDestroy() {
        _tabMediator?.detach()
        super.onDestroy()
        _binding = null
    }
}