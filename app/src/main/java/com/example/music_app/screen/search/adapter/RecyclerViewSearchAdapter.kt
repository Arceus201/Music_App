package com.example.music_app.screen.search.adapter

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.music_app.data.model.Song
import com.example.music_app.databinding.ItemLoaderBinding
import com.example.music_app.databinding.ItemSongBinding
import com.example.music_app.utils.Constant
import com.example.music_app.utils.ItemClickListener
import com.example.music_app.utils.loadByGlide

class RecyclerViewSearchAdapter(private val itemClick: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listSong = mutableListOf<Song>()
    private var isLoading = false

    fun setData(songs: List<Song>) {
        listSong.clear()
        listSong.addAll(songs)
        notifyDataSetChanged()
    }

    fun addFooterLoading() {
        isLoading = true
        listSong.add(Song())
    }

    fun addData(songs: List<Song>) {
        isLoading = false
        listSong.removeAt(listSong.size - 1)
        listSong.addAll(songs)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (listSong.size > 0 && position == listSong.size - 1 && isLoading) return TYPE_LOAD
        return TYPE_SONG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_LOAD) {
            val binding =
                ItemLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(binding)
        }
        val binding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_SONG) {
            val holderSong = holder as SongViewHolder
            holderSong.binding.apply {
                textviewSongName.text = listSong.get(position).songDetail.songName
                textviewArtistName.text = listSong.get(position).songDetail.songArtist
            }
            val imgSong =
                if (listSong.get(position).isLocal) ContentUris.withAppendedId(
                    Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                    listSong.get(position).songDetail.songImg.toLong()
                )
                else listSong.get(position).songDetail.songImg.toUri()
            holder.binding.imgSong.loadByGlide(holder.binding.root.context, imgSong)
        }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class SongViewHolder(var binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                itemClick.onItemClick(listSong.get(adapterPosition))
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemLoaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // TODO no-op
    }
    interface ItemClickListener {
        fun onItemClick(song: Song)
    }

    companion object {
        private const val TYPE_SONG = 1
        private const val TYPE_LOAD = 2
    }
}