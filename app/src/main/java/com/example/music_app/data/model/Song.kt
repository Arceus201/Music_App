package com.example.music_app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Song(
    var songDetail: SongDetail = SongDetail(),
    var isLocal: Boolean = false,
    var isFavorite: Boolean = false,
    var lyrics: String = ""
) :  Parcelable{
    companion object {
        const val SONG_ID = "songid"
        const val SONG_FAVORITE = "songfavorite"
    }
}


