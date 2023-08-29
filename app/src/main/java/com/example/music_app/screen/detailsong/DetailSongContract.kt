package com.example.music_app.screen.detailsong

import com.example.music_app.data.model.Song
import com.example.music_app.utils.base.BasePresenter

interface DetailSongContract {
    interface View {
        fun displayCurrentSong(song: Song)
        fun displayCurrentTimeSong(time: Int)
        fun displayLyricSong(lyric: MutableList<String>)
        fun displayPlayOrPause(isPlaying: Boolean)
        fun displayFavorite(isFavorite: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun getCurrentSong()
        fun handleChangeSeekBar(value: Int)
        fun getLyrics()
        fun handleEventFavorite()
    }
}