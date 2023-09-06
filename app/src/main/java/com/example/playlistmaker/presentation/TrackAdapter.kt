package com.example.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.OnTrackClickListener
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.TrackResult
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(private val tracks: List<TrackResult>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var listener: OnTrackClickListener? = null
    fun setOnTrackClickListener(listener: OnTrackClickListener) {
        this.listener = listener
    }
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val sourceName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.track_artist)
        private val trackLength: TextView = itemView.findViewById(R.id.track_length)
        private val image: ImageView = itemView.findViewById(R.id.track_image)

        fun bind(model: TrackResult) {
            //Log.d(TAG, "${model.id}")
            sourceName.text = model.trackName
            artistName.text = model.artistName
            trackLength.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
            Glide.with(itemView.context).load(model.artworkUrl100)
                .centerCrop()// Отрисовка фотографии артиста с помощью библиотеки Glide
                .error(R.drawable.zaglushka)
                .placeholder(R.drawable.zaglushka)
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corners)))
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
        holder.itemView.setOnClickListener {
            listener?.onTrackClick(position)
        }
    }

}