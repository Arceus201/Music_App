package com.example.music_app.data.repo.resource.remote.fetchjson

import android.annotation.SuppressLint
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.utils.Constant.NO_DATA
import com.example.music_app.utils.Constant.URL_TRENDING_SONG
import com.example.music_app.utils.Constant.ZING_DATA
import com.example.music_app.utils.Constant.ZING_SONG
import com.example.music_app.utils.Constant.ZING_SONG_ID
import com.example.music_app.utils.handler
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetListTrendingSong (private val listen : OnResultListener<MutableList<Song>>){
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    init {
        mExecutor.execute {
            fetchAPI(listen)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fetchAPI(listen: OnResultListener<MutableList<Song>>) {
        val url = URL(URL_TRENDING_SONG)
        val jsonResult = GetJson().getJsonFromGetTrendingSongAPI(url)
        val listSong = mutableListOf<Song>()
        if (jsonResult != null) {
            val resultObject = JSONObject(jsonResult)
            val dataObject = resultObject.getJSONObject(ZING_DATA)
            val arraySongObject = dataObject.getJSONArray(ZING_SONG)
            for (i in 0..arraySongObject.length() - 1) {
                val jsonObject: JSONObject = arraySongObject.get(i) as JSONObject
                val id = jsonObject.getString(ZING_SONG_ID)
//                val song = LocalSong.getInstance().getSong(id)
//                if (song == null) {
                    listSong.add(GetDataSong().getInZingAPI(id))
//                } else listSong.add(song)
            }
            mExecutor.execute {
                handler.post {
                    listen.onSuccess(listSong)
                }
            }
        } else listen.onFail(NO_DATA)
    }
}