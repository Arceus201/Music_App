package com.example.music_app.screen.listsongyourlibrary

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.music_app.data.model.Song
import com.example.music_app.screen.SongService

class ListSongYourPresenter : ListSongYourContract.Presenter {

    private var view: ListSongYourContract.View? = null
    private var isConnected = false
    private lateinit var musicService: SongService
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as SongService.LocalBinder
            musicService = binder.getService()
            isConnected = true
            getCurrentSong()
            view?.displayPlayOrPause(musicService.isPlayings)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun getCurrentSong() {
        if (musicService.listSongs.size == 0) {
            view?.displayCurrentSong(Song())
        } else {
            val song = musicService.listSongs.get(musicService.positions)
            view?.displayCurrentSong(song)
        }
    }

    override fun bindService(context: Context) {
        val intent = Intent(context,SongService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun unBindService(context: Context) {
        if (isConnected) context.unbindService(serviceConnection)
    }

    override fun handleFavorite() {
        view?.displayFavorite(musicService.listSongs.get(musicService.positions).isFavorite)
    }

    override fun handlePlayOrPauseSong() {
        view?.displayPlayOrPause(musicService.isPlayings)
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int) {
        musicService.startSong(list, pos)
    }

    override fun onStart() {
        // TODO("Not yet implemented")
    }

    override fun onStop() {
        // TODO("Not yet implemented")
    }

    override fun setView(view: ListSongYourContract.View?) {
        this.view = view
    }
}