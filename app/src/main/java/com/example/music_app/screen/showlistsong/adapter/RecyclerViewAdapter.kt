package com.example.music_app.screen.showlistsong.adapter

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.music_app.data.model.Song
import com.example.music_app.databinding.ItemSongBinding
import com.example.music_app.utils.Constant
import com.example.music_app.utils.ItemClickListener
import com.example.music_app.utils.loadByGlide

class RecyclerViewAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder?>() {

    private val listSong = mutableListOf<Song>()

    fun setData(listSong: List<Song>) {

        this.listSong.apply {
            clear()
            addAll(listSong)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ItemSongBinding.inflate(inflater, parent, false)
        return ViewHolder(viewBinding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.apply {
            textviewArtistName.text= listSong.get(position).songDetail.songArtist
            textviewSongName.text = listSong.get(position).songDetail.songName
        }
        val imgSong =
            if (listSong.get(position).isLocal) ContentUris.withAppendedId(
                Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                listSong.get(position).songDetail.songImg.toLong()
            )
            else listSong.get(position).songDetail.songImg.toUri()
        holder.viewBinding.imgSong.loadByGlide(holder.viewBinding.root.context, imgSong)
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class ViewHolder(var viewBinding: ItemSongBinding, listener: ItemClickListener) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition, listSong)
            }
        }
    }
}
