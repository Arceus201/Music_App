package com.example.music_app.screen.detailsong.fragment

import androidx.core.net.toUri
import com.example.music_app.databinding.FragmentSongImageBinding
import com.example.music_app.utils.Constant.TIME_DELAY_FOR_LOAD
import com.example.music_app.utils.Constant.TIME_SLEEP_100
import com.example.music_app.utils.base.BaseFragment
import com.example.music_app.utils.handler
import com.example.music_app.utils.loadByGlide

class ImageSongFragment :
BaseFragment<FragmentSongImageBinding>(FragmentSongImageBinding::inflate){
    private var stateRotate = 0

    override fun initView() {
    }

    override fun initData() {
    }

    fun displayImage(imgUrl: String) {
        stateRotate = 0
        handler.postDelayed({
            binding.imageviewSong.loadByGlide(binding.root.context, imgUrl.toUri())
            startAnim()
        }, TIME_DELAY_FOR_LOAD)
    }

    private fun startAnim() {
        handler.post(object : Runnable {
            override fun run() {
                binding.imageviewSong.rotation = stateRotate++.toFloat()
                handler.removeCallbacks(this)
                handler.postDelayed(this, TIME_SLEEP_100)
            }
        })
    }

    fun stopSong() {
        handler.removeCallbacksAndMessages(null)
    }

    fun playSong() {
        startAnim()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}