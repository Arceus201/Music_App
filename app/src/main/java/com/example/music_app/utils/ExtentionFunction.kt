package com.example.music_app.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.music_app.R
import de.hdodenhof.circleimageview.CircleImageView


fun ImageView.loadByGlide(context: Context, uri: Uri) {
    Glide
        .with(context)
        .load(uri)
        .placeholder(R.drawable.img_logo_spotify)
        .error(R.drawable.img_logo_spotify)
        .into(this)
}

fun CircleImageView.loadByGlide(context: Context, uri: Uri) {
    Glide
        .with(context)
        .load(uri)
        .placeholder(R.drawable.img_logo_spotify)
        .error(R.drawable.img_logo_spotify)
        .into(this)
}