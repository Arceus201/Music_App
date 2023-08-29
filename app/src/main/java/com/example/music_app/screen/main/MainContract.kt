package com.example.music_app.screen.main

import android.content.Context
import com.example.music_app.data.model.Song
import com.example.music_app.utils.base.BasePresenter

interface MainContract {
    interface View{
        fun onStartSong(song : Song)
        fun onPlaySong()
        fun onPauseSong()
        fun displayFavotite()
        fun displayUnFavorite()
    }

    interface  Presenter : BasePresenter<View>{
        fun handleNextSong(context: Context)
        fun handlePlayOrPauseSong(context: Context)
        fun handlePreviousSong(context: Context)
        fun handleStartSong(context: Context)
        fun handleFavoriteSong(context: Context)
    }
}