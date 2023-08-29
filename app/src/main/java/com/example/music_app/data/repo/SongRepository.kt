package com.example.music_app.data.repo

import android.content.Context
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.data.repo.resource.SongDataSource
import java.net.URL

class SongRepository private constructor(
    val local: SongDataSource.Local,
    val remote: SongDataSource.Remote
) :
    SongDataSource.Local,
    SongDataSource.Remote{
    override fun getListSongLocal(context: Context?, listen: OnResultListener<MutableList<Song>>) {
        local.getListSongLocal(context,listen)
    }

    override fun getListSongRecent(context: Context?, listen: OnResultListener<MutableList<Song>>) {
        local.getListSongRecent(context,listen)
    }

    override fun getListSongFavorite(listen: OnResultListener<MutableList<Song>>) {
       local.getListSongFavorite(listen)
    }

    override fun getSong(id: String): Song? {
        return local.getSong(id)
    }

    override fun addSongRecent(song: Song) {
        local.addSongRecent(song)
    }

    override fun addSongFavorite(song: Song) {
       local.addSongFavorite(song)
    }

    override fun remoteSongFavorite(song: Song) {
        local.remoteSongFavorite(song)
    }

    override fun getListTrendingSong(listen: OnResultListener<MutableList<Song>>) {
       remote.getListTrendingSong(listen)
    }

    override fun getListSearchSong(url: URL, listen: OnResultListener<MutableList<Song>>) {
       remote.getListSearchSong(url,listen)
    }

    override fun getLyricSong(url: URL, listen: OnResultListener<MutableList<String>>) {
       remote.getLyricSong(url,listen)
    }

    companion object{
        private var instance: SongRepository? = null
        fun getInstance(
            local: SongDataSource.Local,
            remote: SongDataSource.Remote
        ) = synchronized(this){
            instance?: SongRepository(local,remote).also { instance = it }
        }
    }
}