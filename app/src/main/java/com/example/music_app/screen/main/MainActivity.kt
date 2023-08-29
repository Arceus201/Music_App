package com.example.music_app.screen.main

import android.content.*
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.music_app.R
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.local.LocalSong
import com.example.music_app.data.repo.resource.remote.RemoteSong
import com.example.music_app.databinding.ActivityMainBinding
import com.example.music_app.screen.SongService
import com.example.music_app.screen.detailsong.DetailSongActivity
import com.example.music_app.screen.main.adapter.ViewPagerMainAdapter
import com.example.music_app.screen.search.SearchActivity
import com.example.music_app.screen.yourlibrary.YourLibraryFragment
import com.example.music_app.utils.Constant
import com.example.music_app.utils.MusicAction
import com.example.music_app.utils.base.BaseActivity
import com.example.music_app.utils.loadByGlide
import com.example.zingmp3phake.screen.explore.ShowListSongFragment

class MainActivity :
    BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    MainContract.View {
    private lateinit var presenter: MainPresenter
    private var localReciver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            presenter.apply {
                when (intent?.getStringExtra(Constant.ACTION_MUSIC)) {
                    MusicAction.START.name -> handleStartSong(applicationContext)
                    MusicAction.NEXT.name -> handleNextSong(applicationContext)
                    MusicAction.PLAYORPAUSE.name -> handlePlayOrPauseSong(applicationContext)
                    MusicAction.PERVIOUS.name -> handlePreviousSong(applicationContext)
                    MusicAction.FAVORITE.name -> handleFavoriteSong(applicationContext)
                    else -> {}
                }
            }
        }
    }
    override fun initView() {

    }

    override fun initData() {
        presenter = MainPresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance()
            )
        )
        presenter.setView(this)

    }

    override fun handleEvent() {
        val filter = IntentFilter(Constant.ACTION_MUSIC_BROADCAST)
        registerReceiver(localReciver, filter)
        val intent = Intent(applicationContext.applicationContext, SongService::class.java)
        bindService(intent, presenter.serviceConnection, Context.BIND_AUTO_CREATE)
        binding.apply {
            viewPagerMain.adapter = ViewPagerMainAdapter(
                supportFragmentManager,
                listOf<Fragment>(ShowListSongFragment()
                    , YourLibraryFragment()
                )
            )
            navBar.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_your_library -> {
                        binding.viewPagerMain.currentItem = 1
                        return@setOnItemSelectedListener true
                    }
                    else -> {
                        binding.viewPagerMain.currentItem = 0
                        return@setOnItemSelectedListener true
                    }
                }
            }
            viewPagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // TODO nerver implement
                }

                override fun onPageSelected(position: Int) {
                    if (position == 0) navBar.menu.findItem(R.id.menu_home).isChecked = true
                    else navBar.menu.findItem(R.id.menu_your_library).isChecked = true
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // TODO never implement
                }
            })
            buttonFavorite.setOnClickListener {
                presenter.handleFavoriteSong(applicationContext)
            }
            buttonPlay.setOnClickListener {
                presenter.handlePlayOrPauseSong(applicationContext)
            }
            buttonNext.setOnClickListener {
                presenter.handleNextSong(applicationContext)
            }
            containerStateSong.setOnClickListener {
                val intent = Intent(applicationContext, DetailSongActivity::class.java)
                startActivity(intent)
            }
            containerSearchview.setOnClickListener {
                val intent = Intent(applicationContext, SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onStartSong(song: Song) {
        binding.apply {
            textSongName.text = song.songDetail.songName
            textArtistName.text = song.songDetail.songArtist
            containerStateSong.visibility = View.VISIBLE
            buttonPlay.setImageResource(R.drawable.ic_play_24)
            if (song.isFavorite) buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
            else buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
            if (song.isLocal) {
                val imgUri = ContentUris.withAppendedId(
                    Uri.parse(Constant.MEDIA_EXTERNAL_AUDIO_URI),
                    song.songDetail.songImg.toLong()
                )
                circleImageSong.loadByGlide(applicationContext, imgUri)
            } else {
                circleImageSong.loadByGlide(applicationContext, song.songDetail.songImg.toUri())
            }
        }

    }

    override fun onPlaySong() {
        binding.buttonPlay.setImageResource(R.drawable.ic_play_24)
    }

    override fun onPauseSong() {
        binding.buttonPlay.setImageResource(R.drawable.ic_pause_24)
    }



    override fun displayFavotite() {
        binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_24)
    }

    override fun displayUnFavorite() {
        binding.buttonFavorite.setImageResource(R.drawable.ic_unfavorite_24)
    }

    override fun onDestroy() {
        unbindService(presenter.serviceConnection)
        unregisterReceiver(localReciver)
        super.onDestroy()
    }


}