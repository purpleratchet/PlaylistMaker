package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val sourceName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.track_artist)
        private val trackLength: TextView = itemView.findViewById(R.id.track_length)
        private val image: ImageView = itemView.findViewById(R.id.track_image)

        fun bind(model: Track) {
            sourceName.text = model.trackName
            artistName.text = model.artistName
            trackLength.text = model.trackTime
            Glide.with(itemView.context).load(model.artworkUrl100)
                .centerCrop()// Отрисовка фотографии артиста с помощью библиотеки Glide
                .error(R.drawable.zaglushka)
                .transform(RoundedCorners(10))
                .into(image)
        }
    }

    override fun getItemCount(): Int = tracks.size // Количество элементов в списке данных

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
    }

}