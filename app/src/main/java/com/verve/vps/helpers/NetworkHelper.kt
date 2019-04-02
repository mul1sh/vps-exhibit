package com.verve.vps.helpers

import android.content.Context
import android.net.ConnectivityManager

object NetworkHelper{

    internal fun checkInternetConnectivity(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    @JvmStatic
    internal var playstoreVersionUpdated : Boolean = false


    @JvmStatic
    internal var connectedToFirebase : Boolean = false
}