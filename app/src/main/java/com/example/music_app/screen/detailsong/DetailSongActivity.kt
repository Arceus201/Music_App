package com.example.music_app.screen.detailsong

import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.music_app.R
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.local.LocalSong
import com.example.music_app.data.repo.resource.remote.RemoteSong
import com.example.music_app.databinding.ActivityDetailSongBinding
import com.example.music_app.screen.SongService
import com.example.music_app.screen.detailsong.adapter.ViewPagerDetailSongAdapter
import com.example.music_app.screen.detailsong.fragment.ImageSongFragment
import com.example.music_app.screen.detailsong.fragment.LyricSongFragment
import com.example.music_app.utils.*
import com.example.music_app.utils.Constant.START_TIME
import com.example.music_app.utils.Constant.TIME_DELAY_FOR_LOAD
import com.example.music_app.utils.base.BaseActivity
import kotlin.random.Random

class DetailSongActivity :
    BaseActivity<ActivityDetailSongBinding>(ActivityDetailSongBinding::inflate),
    DetailSongContract.View {
    private lateinit var presenter: DetailSongPresenter
    private val detailImageFragment = ImageSongFragment()
    private val lyricsDetailFragment = LyricSongFragment()

    var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            when (intent?.getStringExtra(Constant.ACTION_MUSIC)) {
                MusicAction.START.name -> {
                    handler.removeCallbacksAndMessages(null)
                    presenter.getTimeforView()
                    presenter.getCurrentSong()
                }
                MusicAction.PLAYORPAUSE.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getTimeforView()
                        presenter.handlePlayOrPause()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.NEXT.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getCurrentSong()
                        presenter.getTimeforView()
                    }, TIME_DELAY_FOR_LOAD)
                }
                MusicAction.PERVIOUS.name -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({
                        presenter.getCurrentSong()
                        presenter.getTimeforView()
                    }, TIME_DELAY_FOR_LOAD)
                }
                else -> {}
            }
        }
    }

    override fun initView() {
        binding.apply {
            viewPager.adapter = ViewPagerDetailSongAdapter(
                supportFragmentManager,
                listOf<Fragment>(detailImageFragment, lyricsDetailFragment)
            )
        }
    }

    override fun initData() {
        presenter = DetailSongPresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance()
            )
        )
        presenter.setView(this)
    }

    override fun handleEvent() {
        presenter.apply {
            val intent = Intent(applicationContext.applicationContext, SongService::class.java)
            bindService(intent, presenter.serviceConnection, Context.BIND_AUTO_CREATE)
            val filter = IntentFilter(Constant.ACTION_MUSIC_BROADCAST)
            registerReceiver(localReciver, filter)
            val service = Intent(applicationContext, SongService::class.java)
            bindService(service, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        binding.apply {
            buttonBack.setOnClickListener {
                finish()
            }
            buttonPlay.setOnClickListener {
                getPendingIntent(MusicAction.PLAYORPAUSE.name)?.send()
            }
            buttonFavo.setOnClickListener {
                getPendingIntent(MusicAction.FAVORITE.name)?.send()
                presenter.handleEventFavorite()
            }
            buttonNext.setOnClickListener {
                getPendingIntent(MusicAction.NEXT.name)?.send()
                binding.buttonPlay.setImageResource(R.drawable.ic_play_40)
            }
            buttonPre.setOnClickListener {
                getPendingIntent(MusicAction.PERVIOUS.name)?.send()
                binding.buttonPlay.setImageResource(R.drawable.ic_play_40)
            }
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    // TODO("Not yet implemented")
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    // TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (p0 != null) {
                        presenter.handleChangeSeekBar(p0.progress)
                    }
                }
            })
        }
    }



    override fun displayCurrentSong(song: Song) {
        binding.apply {
            textviewSongName.text = song.songDetail.songName
            textviewArtistName.text = song.songDetail.songArtist
            textviewEndTime.text = getTimetoSecond(song.songDetail.songDuration)
            textviewStartTime.text = START_TIME
            textviewEndTime.text = getTimetoMiliSecond(song.songDetail.songDuration)
            seekbar.max = song.songDetail.songDuration
            if (song.isFavorite) buttonFavo.setImageResource(R.drawable.ic_favorite_40)
            else buttonFavo.setImageResource(R.drawable.ic_unfavorite_40)
        }
        if (song.isLocal) {
            val imgUri = ContentUris.withAppendedId(
                Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                song.songDetail.songImg.toLong()
            )
            detailImageFragment.displayImage(imgUri.toString())
        } else detailImageFragment.displayImage(song.songDetail.songImg)
        presenter.getLyrics()
    }

    override fun displayCurrentTimeSong(time: Int) {
        binding.apply {
            seekbar.progress = time
            textviewStartTime.text = getTimetoMiliSecond(time)
        }
    }

    override fun displayLyricSong(lyric: MutableList<String>) {
        if (lyric.size > 0)
            lyricsDetailFragment.displayLyrics(lyric)
    }

    override fun displayPlayOrPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.buttonPlay.setImageResource(R.drawable.ic_play_40)
            detailImageFragment.playSong()
        } else {
            binding.buttonPlay.setImageResource(R.drawable.ic_pause_40)
            detailImageFragment.stopSong()
        }
    }

    override fun displayFavorite(isFavorite: Boolean) {
        if (isFavorite) binding.buttonFavo.setImageResource(R.drawable.ic_unfavorite_40)
        else binding.buttonFavo.setImageResource(R.drawable.ic_favorite_40)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.apply {
            unregisterReceiver(localReciver)
            unbindService(serviceConnection)
        }
        handler.removeCallbacksAndMessages(null)
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

}