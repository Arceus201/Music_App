package com.example.music_app.screen.yourlibrary

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.OnResultListener
import com.example.music_app.screen.SongService
import com.example.music_app.utils.Constant.ACTION_MUSIC
import com.example.music_app.utils.Constant.ACTION_MUSIC_BROADCAST
import com.example.music_app.utils.Constant.READ_PERMISSION_REQUEST_CODE
import com.example.music_app.utils.MusicAction

class YourLibraryPresenter (
    val songRepo: SongRepository,
    val view: YourLibraryContract.View
) : YourLibraryContract.Presenter {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()
    private var musicService: SongService = SongService()
    private var isConnection = false
    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as SongService.LocalBinder
            musicService = binder.getService()
            isConnection = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnection = false
        }
    }
    private var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    songRepo.local.addSongRecent(
                        musicService.listSongs.get(musicService.positions)
                    )
                    getRecentSong(p0)
                }
                else -> {
                }
            }
        }
    }

    override fun getLocalSong(context: AppCompatActivity) {
        if (checkReadPermission(context.applicationContext)) {
            requestReadPermission(context)
        } else {
            songRepo.getListSongLocal(
                context.applicationContext,
                object : OnResultListener<MutableList<Song>> {
                    override fun onSuccess(list: MutableList<Song>) {
                        listLocalSong = list
                        view.getLocalSongSuccess(list)
                    }

                    override fun onFail(msg: String) {
                        view.getLocalSongFail(msg)
                    }
                }
            )
        }
    }

    override fun getRecentSong(context: Context?) {
        songRepo.getListSongRecent(
            context,
            object : OnResultListener<MutableList<Song>> {
                override fun onSuccess(list: MutableList<Song>) {
                    view.getRecentSong(list)
                    listRecentSong = list
                }

                override fun onFail(msg: String) {
                    // TODO later
                }
            }
        )
    }

    override fun getFavoriteSong(context: Context?) {
        songRepo.getListSongFavorite(object : OnResultListener<MutableList<Song>> {
            override fun onSuccess(list: MutableList<Song>) {
                listFavoriteSong = list
                view.getFavoriteSongSuccess(list)
            }

            override fun onFail(msg: String) {
                // TODO("Not yet implemented")
            }
        })
    }

    override fun handleStartSong(list: MutableList<Song>, pos: Int, context: Context?) {
        if (isConnection) {
            musicService.startSong(list, pos)
        } else {
            val service = Intent(context, SongService::class.java)
            context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun stopService() {
        musicService.unbindService(serviceConnection)
        isConnection = false
    }

    override fun bindService(context: Context?) {
        if (isConnection == false) {
            val service = Intent(context, SongService::class.java)
            context?.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun registerBroadcast(context: Context?) {
        val filter = IntentFilter(ACTION_MUSIC_BROADCAST)
        context?.registerReceiver(localReciver, filter)
    }

    override fun unRegisterBroadcast(context: Context?) {
        context?.unregisterReceiver(localReciver)
    }

    private fun checkReadPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadPermission(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            READ_PERMISSION_REQUEST_CODE
        )
    }
}
