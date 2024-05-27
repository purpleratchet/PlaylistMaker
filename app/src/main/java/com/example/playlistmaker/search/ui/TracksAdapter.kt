package com.example.playlistmaker.search.ui
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.TrackSearchModel

class TracksAdapter(
    private val onLongClickListener: (TrackSearchModel) -> Boolean = {true},
    private val getArtWorkUrl60: Boolean = false,
    private val clickListener: TrackClickListener,
) : RecyclerView.Adapter<TracksViewHolder>() {

    var tracks = ArrayList<TrackSearchModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_track, parent, false)
        return TracksViewHolder(view)
    }
    override fun getItemCount(): Int = tracks.size
    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(tracks[position])
        }
        holder.itemView.setOnLongClickListener { onLongClickListener.invoke(tracks[position]) }
    }

    fun updateTracks(newTracks: List<TrackSearchModel>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
    fun interface TrackClickListener {
        fun onTrackClick(track: TrackSearchModel)
    }
}