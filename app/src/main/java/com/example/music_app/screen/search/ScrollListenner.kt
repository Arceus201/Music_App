package com.example.music_app.screen.search

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class ScrollListenner(
    private val linearLayoutManager: LinearLayoutManager?
) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val itemPerScreen = linearLayoutManager?.childCount
        val totalItem = linearLayoutManager?.itemCount
        val fistVisiableItemPositon = linearLayoutManager?.findFirstVisibleItemPosition()
        if (isLoading() || isEndPage()) return
        if (fistVisiableItemPositon != null && itemPerScreen != null && totalItem != null) {
            if (fistVisiableItemPositon >= 0 && fistVisiableItemPositon + itemPerScreen >= totalItem) {
                loadMore()
            }
        }
    }

    abstract fun loadMore()

    abstract fun isLoading(): Boolean

    abstract fun isEndPage(): Boolean
}