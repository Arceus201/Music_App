package com.example.music_app.data.repo.resource.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.provider.MediaStore
import com.example.music_app.data.model.Song
import com.example.music_app.data.model.SongDetail
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.data.repo.resource.SongDataSource
import com.example.music_app.data.repo.resource.local.dal.SongSqliteHelper
import com.example.music_app.utils.Constant
import com.example.music_app.utils.Constant.INDEX_0
import com.example.music_app.utils.Constant.INDEX_1
import com.example.music_app.utils.Constant.INDEX_2
import com.example.music_app.utils.Constant.INDEX_3
import com.example.music_app.utils.Constant.INDEX_4
import com.example.music_app.utils.Constant.INDEX_5
import com.example.music_app.utils.Constant.INDEX_6
import com.example.music_app.utils.Constant.INDEX_7
import com.example.music_app.utils.Constant.INDEX_8
import com.example.music_app.utils.Constant.TABLE_SONG
import com.example.music_app.utils.handler
import java.util.concurrent.Executors
import java.util.logging.Logger

class LocalSong : SongDataSource.Local{
    private val executor = Executors.newSingleThreadExecutor()
    private var songDb: SongSqliteHelper? = null
    override fun getListSongLocal(context: Context?, listen: OnResultListener<MutableList<Song>>) {
        val mRunnable = object : Runnable {
            override fun run() {
                handleGetSonglocal(context, listen)
            }
        }
        executor.execute(mRunnable)
    }

    override fun getListSongRecent(context: Context?, listen: OnResultListener<MutableList<Song>>) {
        val listSong = mutableListOf<Song>()
        executor.execute {
            val sql = "SELECT * FROM $TABLE_SONG "
            val data = SongSqliteHelper.getInstance(context).getData(sql)
            while (data.moveToNext()) {
                data.also {
                    val id = it.getString(INDEX_0)
                    val name = it.getString(INDEX_1)
                    val artist = it.getString(INDEX_2)
                    val duration = it.getInt(INDEX_3)
                    val url = it.getString(INDEX_4)
                    val img = it.getString(INDEX_5)
                    val local = if (it.getInt(INDEX_6) == 1) true else false
                    val favorite = if (it.getInt(INDEX_7) == 1) true else false
                    val lyric = it.getString(INDEX_8)
                    listSong.add(
                        Song(
                            SongDetail(id, name, artist, duration, url, img), local, favorite, lyric
                        )
                    )
                }
            }
            executor.execute {
                handler.post {
                    if (listSong.size == 0) {
                        listen.onFail(Constant.NO_DATA)
                    } else listen.onSuccess(listSong)
                }
            }
        }
    }

    override fun getListSongFavorite(listen: OnResultListener<MutableList<Song>>) {
        val listSong = mutableListOf<Song>()
        executor.execute {
            val sql = "SELECT * FROM $TABLE_SONG WHERE ${Song.SONG_FAVORITE} =1"
            val data = songDb?.getData(sql)
            while (data != null && data.moveToNext()) {
                data.also {
                    val id = it.getString(INDEX_0)
                    val name = it.getString(INDEX_1)
                    val artist = it.getString(INDEX_2)
                    val duration = it.getInt(INDEX_3)
                    val url = it.getString(INDEX_4)
                    val img = it.getString(INDEX_5)
                    val local = if (it.getInt(INDEX_6) == 1) true else false
                    val favorite = if (it.getInt(INDEX_7) == 1) true else false
                    val lyric = it.getString(INDEX_8)
                    listSong.add(
                        Song(
                            SongDetail(id, name, artist, duration, url, img), local, favorite, lyric
                        )
                    )
                }
            }
            executor.execute {
                handler.post {
                    if (listSong.size == 0) {
                        listen.onFail(Constant.NO_DATA)
                    } else listen.onSuccess(listSong)
                }
            }
        }
    }

    override fun getSong(id: String): Song? {
        var song: Song? = null
        executor.execute {
            val sql = "SELECT * FROM $TABLE_SONG WHERE ${Song.SONG_FAVORITE} =1;"
            val data = songDb?.getData(sql)
            if (data != null) {
                while (data.moveToNext()) {
                    data.also {
                        val id = it.getString(INDEX_0)
                        val name = it.getString(INDEX_1)
                        val artist = it.getString(INDEX_2)
                        val duration = it.getInt(INDEX_3)
                        val url = it.getString(INDEX_4)
                        val img = it.getString(INDEX_5)
                        val local = if (it.getInt(INDEX_6) == 1) true else false
                        val favorite = if (it.getInt(INDEX_7) == 1) true else false
                        val lyric = it.getString(INDEX_8)
                        song = Song(
                            SongDetail(id, name, artist, duration, url, img), local, favorite, lyric
                        )
                    }
                }
            }
        }
        return song
    }

    override fun addSongRecent(song: Song) {
        executor.execute {
            try {
                val isLocal = if (song.isLocal) 1 else 0
                val isfavorite = if (song.isFavorite) 1 else 0
                val sql =
                    "INSERT INTO $TABLE_SONG VALUES ('${song.songDetail.songid}',  '${song.songDetail.songName}', '${song.songDetail.songArtist}', ${song.songDetail.songDuration}, '${song.songDetail.songUrl}', '${song.songDetail.songImg}', $isLocal, $isfavorite, '${song.lyrics}');"
                songDb?.queryData(sql)
            } catch (e: SQLiteConstraintException) {
                Logger.getLogger(e.toString())
            }
        }
    }

    override fun addSongFavorite(song: Song) {
        executor.execute {
            val sql =
                "update $TABLE_SONG set ${Song.SONG_FAVORITE} = 1 where ${Song.SONG_ID} = '${song.songDetail.songid}';"
            songDb?.queryData(sql)
        }
    }

    override fun remoteSongFavorite(song: Song) {
        executor.execute {
            val sql =
                "update $TABLE_SONG set ${Song.SONG_FAVORITE} = 0 where ${Song.SONG_ID} = '${song.songDetail.songid}';"
            songDb?.queryData(sql)
        }
    }

    private fun handleGetSonglocal(context: Context?, listen: OnResultListener<MutableList<Song>>) {
        songDb = SongSqliteHelper.getInstance(context)
        val list = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = context?.contentResolver?.query(uri, null, selection, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val url = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val name = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val img = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                if (url != null) list.add(
                    Song(
                        SongDetail(
                            cursor.getString(id),
                            cursor.getString(name),
                            cursor.getString(artist),
                            cursor.getInt(duration),
                            cursor.getString(url),
                            cursor.getString(img)
                        ), true, false, Constant.N0_LYRIC
                    )
                )
            }
        }
        executor.execute {
            handler.post({
                if (list.size > 0) listen.onSuccess(list)
                else listen.onFail(Constant.NO_DATA)
            })
        }
    }
    companion object {
        private var instance: LocalSong? = null
        fun getInstance() = synchronized(this) {
            instance ?: LocalSong().also { instance = it }
        }
    }
}