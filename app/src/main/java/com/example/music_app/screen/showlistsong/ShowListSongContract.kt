package com.example.zingmp3phake.screen.explore

import android.content.Context
import com.example.music_app.data.model.Song
//import com.example.zingmp3phake.data.model.Song

interface ShowListSongContract {
    interface View {
        fun displaySuccess(list: MutableList<Song>)
        fun displayFail(msg: String)
    }

    interface Presenter {
        fun stopService()
        fun getTrendingSong(context: Context?)
        fun bindService(context: Context)
        fun handlerStartSong(list: MutableList<Song>, pos: Int, context: Context?)
        fun isConnectedInternet(context: Context): Boolean
    }
}
