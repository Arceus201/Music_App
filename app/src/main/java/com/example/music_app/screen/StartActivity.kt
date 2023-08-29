package com.example.music_app.screen

import android.content.Intent
import com.example.music_app.databinding.ActivityStartAppBinding
import com.example.music_app.screen.main.MainActivity
import com.example.music_app.utils.Constant.TIME_DELAY_START_APP
import com.example.music_app.utils.base.BaseActivity
import com.example.music_app.utils.handler

class StartActivity:
    BaseActivity<ActivityStartAppBinding>(ActivityStartAppBinding:: inflate)
{
    override fun initView() {
        handler.postDelayed({
               val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, TIME_DELAY_START_APP)
    }

    override fun initData() {
    }

    override fun handleEvent() {
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}