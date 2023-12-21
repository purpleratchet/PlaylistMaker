package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewPager()
        setupTabLayout()
        setupBackButton()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)
    }

    private fun setupTabLayout() {
        tabMediator =
            TabLayoutMediator(binding.libraryTabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorites_tracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabMediator.attach()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

}