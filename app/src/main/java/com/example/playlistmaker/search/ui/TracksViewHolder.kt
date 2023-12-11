package com.example.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.LayoutTrackBinding
import com.example.playlistmaker.search.domain.model.TrackSearchModel

class TracksViewHolder(
    private val binding: LayoutTrackBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: TrackSearchModel) {
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = model.formatTrackDuration()

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.trackArtwork)
    }
}