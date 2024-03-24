package com.example.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.TrackSearchModel

class TracksViewHolder(
    itemView: View
) :
    RecyclerView.ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.trackArtwork)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(model: TrackSearchModel) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.playlist_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(2F, itemView.context))
            )
            .into(image)

        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTimeMillis
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}