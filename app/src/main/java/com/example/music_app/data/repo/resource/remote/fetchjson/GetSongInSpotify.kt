package com.example.music_app.data.repo.resource.remote.fetchjson

import android.util.Log
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.utils.Constant
import com.example.music_app.utils.handler
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetSongInSpotify<T>(
    private val listener: OnResultListener<T>,
    private val key: String,
    private val url: URL
) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    init {
        mExecutor.execute {
            fetchAPI()
        }
    }

    private fun fetchAPI() {
        val result = GetJson().getJonFromSpotifyAPI(url)
        try {
            if (result != null) {
                val jsonObject = JSONObject(result)
                val data = GetDataSong().parseToData(jsonObject, key) as T
                handler.post {
                    if (data == null) listener.onFail(Constant.NO_DATA)
                    else listener.onSuccess(data)
                }
            }
        } catch (e: JSONException) {
            Log.v(Constant.TAG_LOG, e.toString())
            handler.post {
                listener.onFail(Constant.NO_DATA)
            }
        }
    }
}
