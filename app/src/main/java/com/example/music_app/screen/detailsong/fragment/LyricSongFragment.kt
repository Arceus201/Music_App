package com.example.music_app.screen.detailsong.fragment

import androidx.core.net.toUri
import com.example.music_app.databinding.FragmentLyricsBinding
import com.example.music_app.utils.Constant.N0_LYRIC
import com.example.music_app.utils.Constant.TIME_DELAY_FOR_LOAD
import com.example.music_app.utils.base.BaseFragment
import com.example.music_app.utils.handler

class LyricSongFragment :
BaseFragment<FragmentLyricsBinding>(FragmentLyricsBinding::inflate){

    override fun initView() {

    }

    override fun initData() {

    }

    fun displayLyrics(lyrics: MutableList<String>) {
        handler.postDelayed({
            var lyric = ""
            for (x in lyrics) {
                lyric += x + "\n"
            }
            if (lyric.length == 0) binding.textviewLyric.text = N0_LYRIC
            else binding.textviewLyric.text = lyric
        }, TIME_DELAY_FOR_LOAD)
    }
}