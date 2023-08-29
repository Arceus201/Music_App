package com.example.music_app.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.music_app.R
import com.example.music_app.data.model.Song
import com.example.music_app.utils.Constant
import com.example.music_app.utils.MusicAction
import java.io.FileNotFoundException
import java.lang.reflect.InvocationTargetException
import kotlin.random.Random

class SongService : Service(){
    var listSongs = mutableListOf<Song>()
    var positions = 0
    var isPlayings = false
    private var mediaPlayer: MediaPlayer? = null
    private val binder = LocalBinder()
    private var isCreate = false
    private var notification: NotificationCompat.Builder? = null

    inner class LocalBinder : Binder() {
        fun getService(): SongService = this@SongService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun sendNotifications(song: Song) {
        notification?.apply {
            setContentTitle(song.songDetail.songName)
            setContentText(song.songDetail.songArtist)
            clearActions()
            if (isPlayings) addAction(
                R.drawable.ic_play_24,
                Constant.TITILE_PLAY,
                getPendingIntent(MusicAction.PLAYORPAUSE.name)
            )
            else addAction(
                R.drawable.ic_pause_24,
                Constant.TITILE_PAUSE,
                getPendingIntent(MusicAction.PLAYORPAUSE.name)
            )
            addAction(
                R.drawable.ic_next_24,
                Constant.TITILE_NEXT,
                getPendingIntent(MusicAction.NEXT.name)
            )
        }
        var bitmap: Bitmap? = null
        if (listSongs.get(positions).isLocal) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            this.contentResolver,
                            ContentUris.withAppendedId(
                                Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                                song.songDetail.songImg.toLong()
                            )
                        )
                    )
                }
            } catch (e: InvocationTargetException) {
                e.message?.let { Log.v(Constant.TAG_LOG, it) }
                bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.img_logo_spotify)
            } catch (e: FileNotFoundException) {
                e.message?.let { Log.v(Constant.TAG_LOG, it) }
                bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.img_logo_spotify)
            }
            notification?.setLargeIcon(bitmap)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(Constant.NOTIFICATION_ID, notification?.build())
            } else {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(Constant.NOTIFICATION_ID, notification?.build())
            }
        } else {
            Glide.with(applicationContext).asBitmap()
                .load(listSongs.get(positions).songDetail.songImg)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        notification?.setLargeIcon(resource)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForeground(Constant.NOTIFICATION_ID, notification?.build())
                        } else {
                            val notificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(
                                Constant.NOTIFICATION_ID,
                                notification?.build()
                            )
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // TODO later
                    }
                })
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(Constant.CHANNEL_ID, Constant.CHANNEL_NAME, importance)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntent(action: String): PendingIntent? {
        val intent = Intent(Constant.ACTION_MUSIC_BROADCAST)
        intent.putExtra(Constant.ACTION_MUSIC, action)
        return PendingIntent.getBroadcast(
            applicationContext,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun playOrPause() {
        if (isPlayings) {
            mediaPlayer?.pause()
            isPlayings = false
            sendNotifications(listSongs.get(positions))
        } else {
            mediaPlayer?.start()
            isPlayings = true
            sendNotifications(listSongs.get(positions))
        }
    }

    fun onChangeSeekBar(value: Int) {
        mediaPlayer?.seekTo(value)
    }

    fun startSong(listSong: MutableList<Song>, pos: Int) {
        this.listSongs = listSong
        positions = pos
        isPlayings = true
        getPendingIntent(MusicAction.START.name)?.send()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        if (listSong.get(pos).isLocal) {
            mediaPlayer?.apply {
                setDataSource(applicationContext, listSong.get(positions).songDetail.songUrl.toUri())
            }
        } else {
            mediaPlayer?.apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(listSong.get(pos).songDetail.songUrl)
            }
        }
        mediaPlayer?.apply {
            isLooping = false
            setOnCompletionListener {
                getPendingIntent(MusicAction.NEXT.name)?.send()
            }
            prepare()
            start()
        }
        if (isCreate == false) {
            createNotifi()
        }
        sendNotifications(listSong.get(positions))
    }

    private fun createNotifi() {
        isCreate = true
        val mediaSession = MediaSessionCompat(this, Constant.TAG_MEDIA_SESSION)
        notification = NotificationCompat.Builder(this, Constant.CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_favorite_24)
                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1)
                        .setMediaSession(mediaSession.sessionToken)
                )
                if (isPlayings) addAction(
                    R.drawable.ic_play_24,
                    Constant.TITILE_PLAY,
                    getPendingIntent(MusicAction.PLAYORPAUSE.name)
                )
                else addAction(
                    R.drawable.ic_pause_24,
                    Constant.TITILE_PAUSE,
                    getPendingIntent(MusicAction.PLAYORPAUSE.name)
                )
                addAction(
                    R.drawable.ic_next_24,
                    Constant.TITILE_NEXT,
                    getPendingIntent(MusicAction.NEXT.name)
                )
            }
        createNotificationChannel()
    }

    fun getCurrentSongTime(): Int? = mediaPlayer?.currentPosition
}