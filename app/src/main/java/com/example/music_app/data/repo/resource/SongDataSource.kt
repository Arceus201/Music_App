package com.example.music_app.data.repo.resource

import android.content.Context
import com.example.music_app.data.model.Song
import java.net.URL

interface SongDataSource {
    interface Local{
        fun getListSongLocal(context: Context?, listen: OnResultListener<MutableList<Song>>)
        fun getListSongRecent(context: Context?, listen: OnResultListener<MutableList<Song>>)
        fun getListSongFavorite(listen: OnResultListener<MutableList<Song>>)
        fun getSong(id: String): Song?

        fun addSongRecent(song: Song)
        fun addSongFavorite(song: Song)
        fun remoteSongFavorite(song:Song)
    }

    interface Remote{
        fun getListTrendingSong(listen: OnResultListener<MutableList<Song>>)
        fun getListSearchSong(url: URL, listen: OnResultListener<MutableList<Song>>)
        fun getLyricSong(url: URL, listen: OnResultListener<MutableList<String>>)
    }
}