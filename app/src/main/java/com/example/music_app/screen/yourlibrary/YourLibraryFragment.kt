package com.example.music_app.screen.yourlibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.local.LocalSong
import com.example.music_app.data.repo.resource.remote.RemoteSong
import com.example.music_app.databinding.FragmentYourLibraryBinding
import com.example.music_app.screen.listsongyourlibrary.ListSongYourLibraryActivity
import com.example.music_app.screen.yourlibrary.adapter.RecyclerViewRecentAdapter
import com.example.music_app.utils.Constant.BUNDLE_LIST_KEY
import com.example.music_app.utils.Constant.BUNDLE_TITLE_STRING_KEY
import com.example.music_app.utils.Constant.DATA_KEY
import com.example.music_app.utils.Constant.SONG
import com.example.music_app.utils.Constant.TITLE_FAVORITE
import com.example.music_app.utils.Constant.TITLE_LOCAL
import com.example.music_app.utils.base.BaseFragment

class YourLibraryFragment :
    BaseFragment<FragmentYourLibraryBinding>(FragmentYourLibraryBinding::inflate),
    YourLibraryContract.View,
    RecyclerViewRecentAdapter.ItemClickListener {

    private var listLocalSong = mutableListOf<Song>()
    private var listRecentSong = mutableListOf<Song>()
    private var listFavoriteSong = mutableListOf<Song>()
    private var presenter = YourLibraryPresenter(
        SongRepository.getInstance(
            LocalSong.getInstance(),
            RemoteSong.getInstance()
        ),
        this
    )
    private val adapterRecentSong = RecyclerViewRecentAdapter(this)

    override fun initView() {
        binding.recyclerViewSongRecent.isNestedScrollingEnabled = true
        binding.recyclerViewSongRecent.adapter = adapterRecentSong
        binding.apply {
            containerLocal.setOnClickListener {
                val intent = Intent(context?.applicationContext, ListSongYourLibraryActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listLocalSong as ArrayList<Song>)
                bundle.putString(BUNDLE_TITLE_STRING_KEY, TITLE_LOCAL)
                intent?.putExtra(DATA_KEY, bundle)
                intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
            containerFavorite.setOnClickListener {
                presenter?.getFavoriteSong(context)
            }
        }
    }

    override fun initData() {
        presenter?.getLocalSong(context as AppCompatActivity)
        presenter?.getRecentSong(context)
        presenter?.registerBroadcast(context)
        presenter?.bindService(context)
    }

    override fun getLocalSongSuccess(songs: MutableList<Song>) {
        binding.textNumberOfLocalSong.text = "${songs.size} $SONG"
        listLocalSong = songs
    }

    override fun getLocalSongFail(msg: String) {
        Toast.makeText(context?.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getRecentSong(songs: MutableList<Song>) {
        adapterRecentSong.setData(songs)
        listRecentSong = songs
    }

    override fun getFavoriteSongSuccess(songs: MutableList<Song>) {
        listFavoriteSong = songs
        val intent = Intent(context?.applicationContext, ListSongYourLibraryActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList(BUNDLE_LIST_KEY, listFavoriteSong as ArrayList<Song>)
        bundle.putString(BUNDLE_TITLE_STRING_KEY, TITLE_FAVORITE)
        intent?.putExtra(DATA_KEY, bundle)
        intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        presenter?.handleStartSong(listSong, pos, context)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stopService()
        presenter?.unRegisterBroadcast(context)
    }
}
