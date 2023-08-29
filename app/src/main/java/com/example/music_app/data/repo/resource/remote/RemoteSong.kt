package com.example.music_app.data.repo.resource.remote

import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.data.repo.resource.SongDataSource
import com.example.music_app.data.repo.resource.remote.fetchjson.GetListTrendingSong
import com.example.music_app.data.repo.resource.remote.fetchjson.GetSongInSpotify
import com.example.music_app.utils.Constant
import java.net.URL

class RemoteSong  : SongDataSource.Remote{
    override fun getListTrendingSong(listen: OnResultListener<MutableList<Song>>) {
        GetListTrendingSong(listen)
    }

    override fun getListSearchSong(url: URL, listen: OnResultListener<MutableList<Song>>) {
        GetSongInSpotify(listen, Constant.SPOTIFY_TRACK, url)
    }

    override fun getLyricSong(url: URL, listen: OnResultListener<MutableList<String>>) {
        GetSongInSpotify(listen, Constant.SPOTIFY_TRACK, url)
    }

    companion object{
        private var instance: RemoteSong? = null
        fun getInstance() = synchronized(this){
            instance?: RemoteSong().also { instance = it }
        }
    }
}