package com.example.mystructure.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {

    fun checkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}