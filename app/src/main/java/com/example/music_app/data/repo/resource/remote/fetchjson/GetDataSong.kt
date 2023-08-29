package com.example.music_app.data.repo.resource.remote.fetchjson

import android.util.Log
import com.example.music_app.data.model.Song
import com.example.music_app.utils.Constant
import com.example.music_app.utils.Constant.INDEX_0
import com.example.music_app.utils.Constant.MILISECOND_OF_SECOND
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class GetDataSong {
    fun getInZingAPI(id: String?): Song {
        val url = URL(Constant.API_ZING_SONG + id)
        val result = GetJson().getJsonFromGetTrendingSongAPI(url)
        val jsonObject = JSONObject(result)
        val dataObject = jsonObject.getJSONObject(DATA)
        return Song().apply {
            dataObject.let {
                songDetail.songid = it.getString(ZING_SONG_ID)
                songDetail.songName = it.getString(ZING_SONG_NAME)
                songDetail.songDuration = it.getInt(ZING_SONG_DURATION) * MILISECOND_OF_SECOND
                try {
                    val albumObject = it.getJSONObject(ZING_SONG_ALBUM)
                    songDetail.songImg = albumObject.getString(ZING_SONG_IMG)
                } catch (e: JSONException) {
                    Log.v(Constant.TAG_LOG, e.toString())
                    songDetail.songImg = it.getString(ZING_SONG_IMG)
                }
                songDetail.songArtist = it.getString(ZING_SONG_ARTIST)
                val sourceObject = it.getJSONObject(ZING_SONG_SOURCE)
                songDetail.songUrl = sourceObject.getString(ZING_SONG_URL)
                lyrics = Constant.N0_LYRIC
            }
        }
    }
    fun getInSpotify(id: String?): Song {
        val url = URL("${Constant.API_BASE_SPOTIFY}tracks/?ids=$id")
        val result = GetJson().getJonFromSpotifyAPI(url)
        val jsonObject = JSONObject(result)
        val trackObject = jsonObject.getJSONArray(SPOTIFY_TRACK)[INDEX_0] as JSONObject
        val artistObject = trackObject.getJSONArray(SPOTIFY_ARTIST)[INDEX_0] as JSONObject
        return Song().apply {
            songDetail.songid = trackObject.getString(ID)
            songDetail.songName = trackObject.getString(SPOTIFY_NAME)
            songDetail.songArtist = artistObject.getString(SPOTIFY_NAME)
            songDetail.songDuration = trackObject.getInt(SPOTIFY_DURATION)
            songDetail.songUrl = trackObject.getString(SPOTIFY_URL)
        }
    }
    fun parseToData(jsonObject: JSONObject, key: String): Any {
        return when (key) {
            SPOTIFY_LYRIC -> parseToLyric(jsonObject)
            SPOTIFY_TRACK -> parseToTrack(jsonObject)
            else -> {}
        }
    }

    private fun parseToLyric(dataObject: JSONObject): Any {
        val data = mutableListOf<String>()
        val lyricObject = dataObject.getJSONObject(SPOTIFY_LYRIC)
        val arrayLineOject = lyricObject.getJSONArray(SPOTIFY_LINE)
        for (i in 0..arrayLineOject.length() - 1) {
            val lineOject = arrayLineOject.get(i) as JSONObject
            data.add(lineOject.getString(SPOTIFY_WORD))
        }
        return data
    }

    private fun parseToTrack(dataObject: JSONObject): Any {
        val data = mutableListOf<Song>()
        val trackObject = dataObject.getJSONObject(SPOTIFY_TRACK)
        val itemArrayObject = trackObject.getJSONArray(ITEM)
        for (i in 0..itemArrayObject.length() - 1) {
            val dataTrackObject = (itemArrayObject[i] as JSONObject).getJSONObject(DATA)
            val songId = dataTrackObject.getString(ID)
            var songImg: String? = null
            try {
                val albumOject = dataTrackObject.getJSONObject(ALBUM)
                val artObject = albumOject.getJSONObject(ART)
                val sourceObject = artObject.getJSONArray(SOURCE)[INDEX_0] as JSONObject
                songImg = sourceObject.getString(URL)
            } catch (e: JSONException) {
                Log.v(Constant.TAG_LOG, e.toString())
            }
//            val song = LocalSong.getInstance().getSong(songId)
//            if (song == null) {
                data.add(getInSpotify(songId))
                if (songImg != null) data.get(data.size - 1).songDetail.songImg = songImg
//            } else data.add(song)
        }
        return data
    }
    companion object {
        private const val ITEM = "items"
        private const val DATA = "data"
        private const val ID = "id"
        private const val ALBUM = "albumOfTrack"
        private const val ART = "coverArt"
        private const val SOURCE = "sources"
        private const val URL = "url"
        private const val ZING_SONG_ID = "code"
        private const val ZING_SONG_NAME = "title"
        private const val ZING_SONG_ARTIST = "artists_names"
        private const val ZING_SONG_DURATION = "duration"
        private const val ZING_SONG_IMG = "thumbnail"
        private const val ZING_SONG_SOURCE = "source"
        private const val ZING_SONG_ALBUM = "album"
        private const val ZING_SONG_URL = "128"
        private const val SPOTIFY_LINE = "lines"
        private const val SPOTIFY_WORD = "words"
        private const val SPOTIFY_LYRIC = "lyrics"
        private const val SPOTIFY_TRACK = "tracks"
        private const val SPOTIFY_ARTIST = "artists"
        private const val SPOTIFY_DURATION = "duration_ms"
        private const val SPOTIFY_URL = "preview_url"
        private const val SPOTIFY_NAME = "name"
    }
}