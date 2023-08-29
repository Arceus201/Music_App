package com.example.music_app.screen.listsongyourlibrary

import android.content.Context
import com.example.music_app.data.model.Song
import com.example.music_app.utils.base.BasePresenter

interface ListSongYourContract {
    interface View {
        fun displayCurrentSong(song: Song)
        fun displayFavorite(isFavorite: Boolean)
        fun displayPlayOrPause(isPlaying: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun getCurrentSong()
        fun bindService(context: Context)
        fun unBindService(context: Context)
        fun handleFavorite()
        fun handlePlayOrPauseSong()
        fun handleStartSong(list: MutableList<Song>, pos: Int)
    }
}