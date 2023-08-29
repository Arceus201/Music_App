package com.example.music_app.utils

import android.os.Handler
import android.os.Looper
import com.example.music_app.utils.Constant.MINUTE_SECOND
import java.util.*
import java.util.concurrent.TimeUnit

object Constant {
    const val ACTION_MUSIC = "action.music"
    const val ACTION_MUSIC_BROADCAST = "action.music.broadcast"
    const val BUNDLE_LIST_KEY = "list"
    const val BUNDLE_TITLE_STRING_KEY = "title"
    const val CHANNEL_ID = "chanel_id_notification"
    const val CHANNEL_NAME = "Music"
    const val DATA_KEY = "data"
    const val NO_INTERNET = "Không có kết nối mạng"
    const val NOTIFICATION_ID = 1
    const val NO_DATA = "No Data Founded"
    const val N0_LYRIC = "Chưa có lời cho bài hát này"
    const val MEDIA_EXTERNAL_AUDIO_URI = "content://media/external/audio/albumart"
    const val PAGE_SIZE = 10
    const val READ_PERMISSION_REQUEST_CODE = 101
    const val SONG = " Bài hát"
    const val UTF = "utf-8"
    const val TITILE_NEXT = "next"
    const val TITILE_PLAY = "play"
    const val TITILE_PAUSE = "pause"
    const val TITLE_LOCAL = "Danh sách bài hát trên thiết bị"
    const val TITLE_FAVORITE = "Danh sách bài hát yêu thích"
    const val TAG_MEDIA_SESSION = "tag"
    const val TAG_LOG = "AAAAAA"

    /**
    API Constant
     */
    const val URL_TRENDING_SONG =
        "https://mp3.zing.vn/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1"
    const val API_ZING_SONG = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key="
    const val API_BASE_SPOTIFY = "https://spotify23.p.rapidapi.com/"
    const val API_KEY = "X-RapidAPI-Key"
    const val API_HOST = "X-RapidAPI-Host"
    const val METHOD_GET = "GET"
    const val ZING_SONG_ID = "code"
    const val ZING_SONG = "song"
    const val ZING_DATA = "data"
    const val SPOTIFY_LYRIC = "lyrics"
    const val SPOTIFY_TRACK = "tracks"

    /**
    Time Constant
     */
    const val TIME_DELAY_START_APP = 1000L
    const val MILISECOND_OF_SECOND = 1000
    const val TIME_DELAY_FOR_LOAD = 500L
    const val TIME_SLEEP_100 = 100L
    const val MINUTE_SECOND = 60
    const val START_TIME = "00:00"

    const val INDEX_0 = 0
    const val INDEX_1 = 1
    const val INDEX_2 = 2
    const val INDEX_3 = 3
    const val INDEX_4 = 4
    const val INDEX_5 = 5
    const val INDEX_6 = 6
    const val INDEX_7 = 7
    const val INDEX_8 = 8
    const val DB_NAME = "song.db"
    const val DB_VERSION = 3
    const val TABLE_SONG = "tblsong"
    const val DROP_TABLE = "DROP TABLE IF EXISTS tblsong"
    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS tblsong (songid varchar(255) primary key, songname varchar(255), " +
                "songartist varchar(255),songduration int, songurl varchar(255), songimg varchar(255), " +
                "songlocal int, songfavorite int,songlyric varchar(255) )"



}
/**
 * Handler
 */
val handler = Handler(Looper.getMainLooper())
enum class MusicAction {
    START, NEXT, PERVIOUS, PLAYORPAUSE, FAVORITE
}

fun getTimetoMiliSecond(second: Int): String {
    val minute = TimeUnit.MILLISECONDS.toMinutes(second.toLong())
    val second = TimeUnit.MILLISECONDS.toSeconds(second.toLong()) % MINUTE_SECOND
    val time = String.format(Locale.US, "%02d:%02d", minute, second)
    return time
}

fun getTimetoSecond(second: Int): String {
    val minute = TimeUnit.SECONDS.toMinutes(second.toLong())
    val second = TimeUnit.SECONDS.toSeconds(second.toLong()) % MINUTE_SECOND
    val time = String.format(Locale.US, "%02d:%02d", minute, second)
    return time
}