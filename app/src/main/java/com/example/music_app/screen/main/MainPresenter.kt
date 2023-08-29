package com.example.music_app.screen.main

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.screen.SongService
import com.example.music_app.utils.Constant
import com.example.music_app.utils.NetworkUtils

class MainPresenter(val songRepo: SongRepository) :
    MainContract.Presenter {
    private var mainView: MainContract.View? = null
    private var musicService: SongService = SongService()
    private var isConnection = false
    var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as SongService.LocalBinder
            musicService = binder.getService()
            isConnection = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }
    }


    override fun onStart() {

    }

    override fun onStop() {

    }

    override fun setView(view: MainContract.View?) {
        this.mainView = view
    }

    override fun handleNextSong(context: Context) {
        val postion = (musicService.positions + 1) % musicService.listSongs.size
        val song = musicService.listSongs.get(postion)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            Toast.makeText(context?.applicationContext, Constant.NO_INTERNET, Toast.LENGTH_SHORT)
                .show()
            return
        }
        musicService.startSong(musicService.listSongs, postion)
    }

    override fun handlePlayOrPauseSong(context: Context) {
        if (musicService.isPlayings) mainView?.onPauseSong()
        else mainView?.onPlaySong()
        musicService.playOrPause()
    }

    override fun handlePreviousSong(context: Context) {
        val position =
            if (musicService.positions - 1 >= 0) musicService.positions - 1
            else musicService.listSongs.size - 1
        val song = musicService.listSongs.get(position)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            Toast.makeText(context?.applicationContext, Constant.NO_INTERNET, Toast.LENGTH_SHORT)
                .show()
            return
        }
        musicService.startSong(musicService.listSongs, position)
    }

    override fun handleStartSong(context: Context) {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.isLocal == false && NetworkUtils.isNetworkAvailable(context) == false) {
            Toast.makeText(context?.applicationContext, Constant.NO_INTERNET, Toast.LENGTH_SHORT)
                .show()
            return
        }
        mainView?.onStartSong(song)
    }

    override fun handleFavoriteSong(context: Context) {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.isFavorite) {
            mainView?.displayUnFavorite()
            songRepo.remoteSongFavorite(song)
            song.isFavorite = false
        } else {
            mainView?.displayFavotite()
            songRepo.addSongFavorite(song)
            song.isFavorite = true
        }
        musicService.listSongs.set(musicService.positions, song)
    }
}