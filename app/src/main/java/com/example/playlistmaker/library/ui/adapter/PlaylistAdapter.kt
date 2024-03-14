package com.example.playlistmaker.library.ui.adapter
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import java.io.File

class PlaylistAdapter(val context: Context, private val clickListener: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistHolder>() {
    val playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        return PlaylistHolder(parent, context)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener(playlists[position]) }
    }
}

class PlaylistHolder(parent: ViewGroup, val context: Context) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false)
) {
    private val playlistImageView: ImageView = itemView.findViewById(R.id.iv_playlist)
    private val nameOfPlaylistTextView: TextView = itemView.findViewById(R.id.tv_name_of_playlist)
    private val numberOfTracksTextView: TextView = itemView.findViewById(R.id.tv_amount_of_tracks)
    fun bind(playlist: Playlist) {
        if (playlist.filePath.isNotEmpty()) {
            playlistImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            playlistImageView.setImageURI(getUriOfImageFromStorage(playlist.filePath))
        } else {
            playlistImageView.scaleType = ImageView.ScaleType.CENTER
            playlistImageView.setImageResource(R.drawable.placeholder)
        }
        nameOfPlaylistTextView.text = playlist.name
        numberOfTracksTextView.text = pluralizeWord(playlist.amountOfTracks, "трек")
    }
    private fun getUriOfImageFromStorage(fileName: String): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, fileName)
        return file.toUri()
    }
    private fun pluralizeWord(number: Int, word: String): String {
        return when {
            number % 10 == 1 && number % 100 != 11 -> "$number $word"
            number % 10 in 2..4 && (number % 100 < 10 || number % 100 >= 20) -> "$number $word${
                if (word.endsWith(
                        'а'
                    )
                ) "и" else "а"
            }"
            else -> "$number $word${if (word.endsWith('а')) "" else "ов"}"
        }
    }
}