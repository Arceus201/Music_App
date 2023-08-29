package com.example.music_app.screen.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerMainAdapter (fm: FragmentManager, val list : List<Fragment>)
    : FragmentStatePagerAdapter(fm){
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list.get(position)
    }
}