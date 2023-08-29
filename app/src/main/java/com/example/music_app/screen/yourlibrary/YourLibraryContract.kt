package com.example.music_app.screen.yourlibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.music_app.data.model.Song

interface YourLibraryContract {
    interface View {
        fun getLocalSongSuccess(list: MutableList<Song>)
        fun getLocalSongFail(msg: String)
        fun getRecentSong(list: MutableList<Song>)
        fun getFavoriteSongSuccess(list: MutableList<Song>)
    }

    interface Presenter {
        fun getLocalSong(context: AppCompatActivity)
        fun getRecentSong(context: Context?)
        fun getFavoriteSong(context: Context?)
        fun handleStartSong(list: MutableList<Song>, pos: Int, context: Context?)
        fun stopService()
        fun bindService(context: Context?)
        fun registerBroadcast(context: Context?)
        fun unRegisterBroadcast(context: Context?)
    }
}