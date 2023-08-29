package com.example.music_app.screen.detailsong

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.screen.SongService
import com.example.music_app.utils.Constant
import com.example.music_app.utils.Constant.TIME_DELAY_FOR_LOAD
import com.example.music_app.utils.Constant.TIME_SLEEP_100
import com.example.music_app.utils.handler
import java.net.URL

class DetailSongPresenter(val songRepo: SongRepository) :
    DetailSongContract.Presenter {

    private var detailSongView: DetailSongContract.View? = null
    private var isConnected = false
    private lateinit var musicService: SongService
    var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as SongService.LocalBinder
            musicService = binder.getService()
            isConnected = true
            getCurrentSong()
            getTimeforView()
            handler.postDelayed({
                detailSongView?.displayPlayOrPause(musicService.isPlayings)
            }, TIME_DELAY_FOR_LOAD)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }
    fun handlePlayOrPause() {
        detailSongView?.displayPlayOrPause(musicService.isPlayings)
    }


    override fun getCurrentSong() {
        val song = musicService.listSongs.get(musicService.positions)
        detailSongView?.displayCurrentSong(song)
    }

    override fun handleChangeSeekBar(value: Int) {
        musicService.onChangeSeekBar(value)
    }

    override fun getLyrics() {
        val song = musicService.listSongs.get(musicService.positions)
        if (song.lyrics.equals(Constant.N0_LYRIC)) detailSongView?.displayLyricSong(
            mutableListOf(
                Constant.N0_LYRIC
            )
        )
        else {
            val url = URL("${Constant.API_BASE_SPOTIFY}track_lyrics/?id=${song.songDetail.songid}")
            songRepo.remote.getLyricSong(
                url,
                object : OnResultListener<MutableList<String>> {
                    override fun onSuccess(list: MutableList<String>) {
                        detailSongView?.displayLyricSong(list)
                    }

                    override fun onFail(msg: String) {
                        detailSongView?.displayLyricSong(mutableListOf(Constant.N0_LYRIC))
                    }
                }
            )
        }
    }

    override fun handleEventFavorite() {
        getTimeforView()
        detailSongView?.displayPlayOrPause(musicService.isPlayings)
        val song = musicService.listSongs.get(musicService.positions)
        detailSongView?.displayFavorite(song.isFavorite)
    }

    override fun onStart() {
//        TODO("Not yet implemented")
    }

    override fun onStop() {
//        TODO("Not yet implemented")
    }

    override fun setView(view: DetailSongContract.View?) {
        this.detailSongView = view
    }

    fun getTimeforView() {
        handler.post(object : Runnable {
            override fun run() {
                val time = musicService?.getCurrentSongTime()
                if (time != null) {
                    detailSongView?.displayCurrentTimeSong(time)
                } else detailSongView?.displayCurrentTimeSong(0)
                handler.removeCallbacks(this)
                handler.postDelayed(this, TIME_SLEEP_100)
            }
        })
    }
}