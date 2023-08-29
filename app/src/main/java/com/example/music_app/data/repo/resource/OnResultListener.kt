package com.example.music_app.data.repo.resource

interface OnResultListener <T>{
    fun onSuccess(list: T)
    fun onFail(message: String)
}