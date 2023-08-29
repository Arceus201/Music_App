package com.example.music_app.utils

import com.example.music_app.data.model.Song

interface ItemClickListener {
    fun onItemClick(position: Int, list: MutableList<Song>)
}