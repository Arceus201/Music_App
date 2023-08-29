package com.example.music_app.screen.search

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.screen.SongService
import com.example.music_app.utils.Constant
import com.example.music_app.utils.Constant.INDEX_0
import java.net.URL

class SearchPresenter(private val songRepository: SongRepository):
SearchContract.Presenter{
    private var view: SearchContract.View? = null
    private var textQuery: String? = null
    private var offSet = 0
    var isConnected = false
    private lateinit var musicService: SongService
    var serviceConnection = object : ServiceConnection {
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
            view?.displayPlayOrPause(musicService.isPlayings)
        }
    }

    override fun getFisrtResultSearch(query: String?) {
        val songName = java.net.URLEncoder.encode(query, Constant.UTF)
        textQuery = songName.replace("+", "%20")
        val url =
            URL("${Constant.API_BASE_SPOTIFY}search/?q=$textQuery&type=tracks&limit=${Constant.PAGE_SIZE}")
        offSet = 0
        offSet += Constant.PAGE_SIZE
        songRepository.remote.getListSearchSong(
            url,
            object : OnResultListener<MutableList<Song>> {
                override fun onSuccess(songs: MutableList<Song>) {
                    view?.displayResultSearch(songs)
                }

                override fun onFail(msg: String) {
                    view?.displayResultSearch(mutableListOf())
                }
            }
        )
    }

    override fun getMoreResultSearch() {
        val url =
            URL(
                "${Constant.API_BASE_SPOTIFY}search/?q=$textQuery&type=tracks&" +
                        "offset=$offSet&limit=${Constant.PAGE_SIZE}"
            )
        offSet += Constant.PAGE_SIZE
        songRepository.remote.getListSearchSong(
            url,
            object : OnResultListener<MutableList<Song>> {
                override fun onSuccess(songs: MutableList<Song>) {
                    view?.displayResultSearch(songs)
                }

                override fun onFail(msg: String) {
                    view?.displayResultSearch(mutableListOf())
                }
            }
        )
    }

    override fun handlePlayOrPauseSong() {
        view?.displayPlayOrPause(musicService.isPlayings)
    }

    override fun bindService(context: Context) {
        if (isConnected == false) {
            val intent = Intent(context, SongService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun handleStartSong(song: Song) {
        musicService.startSong(mutableListOf(song), INDEX_0)
    }

    override fun onStart() {
//        TODO("Not yet implemented")
    }

    override fun onStop() {
//        TODO("Not yet implemented")
    }

    override fun setView(view: SearchContract.View?) {
        this.view = view
    }
}