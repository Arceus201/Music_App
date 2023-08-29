package com.example.music_app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {
    fun isNetworkAvailable(context: Context?): Boolean {
        var isConnected = false
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        isConnected = true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        isConnected = true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        isConnected = true
                    }
                }
            }
        } else {
            val wifiNetwork = connectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobileNetwork = connectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val isWifiConnected = wifiNetwork?.isConnected
            val isMobileConnected = mobileNetwork?.isConnected
            return isWifiConnected == true || isMobileConnected == true
        }
        return isConnected
    }
}