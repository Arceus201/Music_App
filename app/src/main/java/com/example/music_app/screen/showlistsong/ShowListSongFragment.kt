package com.example.zingmp3phake.screen.explore

import android.view.View
import android.widget.Toast
import com.example.music_app.data.model.Song
import com.example.music_app.data.repo.SongRepository
import com.example.music_app.data.repo.resource.local.LocalSong
import com.example.music_app.data.repo.resource.remote.RemoteSong
import com.example.music_app.databinding.FragmentShowListSongBinding
import com.example.music_app.screen.showlistsong.adapter.RecyclerViewAdapter
import com.example.music_app.utils.Constant.NO_INTERNET
import com.example.music_app.utils.ItemClickListener
import com.example.music_app.utils.base.BaseFragment


class ShowListSongFragment :
    BaseFragment<FragmentShowListSongBinding>(FragmentShowListSongBinding::inflate),
    ShowListSongContract.View,
    ItemClickListener {
    private var presenter: ShowListSongFragmentPresenter? = null
    private val adapter = RecyclerViewAdapter(this)

    override fun initView() {
        binding.recycleViewTrendingSong.adapter = adapter
    }

    override fun initData() {
        presenter = ShowListSongFragmentPresenter(
            SongRepository.getInstance(
                LocalSong.getInstance(),
                RemoteSong.getInstance(),
            ),
            this
        )
        if (context?.let { presenter?.isConnectedInternet(it.applicationContext) } == true) {
            presenter?.getTrendingSong(context)
        } else {
            Toast.makeText(context, NO_INTERNET, Toast.LENGTH_LONG).show()
        }
    }

    override fun displaySuccess(list: MutableList<Song>) {
        adapter.setData(list)
        binding.cardview.visibility = View.GONE
    }

    override fun displayFail(msg: String) {
        // TODO later
    }

    override fun onItemClick(pos: Int, listSong: MutableList<Song>) {
        presenter?.handlerStartSong(listSong, pos, context)
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.stopService()
    }
}
