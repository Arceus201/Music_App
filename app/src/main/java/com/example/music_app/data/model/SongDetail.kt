package com.example.music_app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SongDetail(
    var songid: String? = null,
    var songName: String = "",
    var songArtist: String = "",
    var songDuration: Int = 0,
    var songUrl: String = "",
    var songImg: String = "",
) : Parcelable