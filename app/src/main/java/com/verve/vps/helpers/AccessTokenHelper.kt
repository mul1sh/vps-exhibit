package com.verve.vps.helpers

import io.reactivex.schedulers.Schedulers

object AccessTokenHelper {

    @JvmStatic
    internal fun handleAccessTokenExpiry()
            = ApiServiceHelper.apiService!!
            .loginUser(PreferencesHelper.sharedPreferences.getString(Constants.LOGIN_TOKEN, "")!!)
            .subscribeOn(Schedulers.io())
            .blockingSingle()

    @JvmStatic
    internal fun getAccessToken() = """Bearer ${PreferencesHelper.sharedPreferences.getString(Constants.AUTH_TOKEN, "")!!}""".trimIndent()

    @JvmStatic
    internal fun saveAccessToken(accessToken: String) {
        PreferencesHelper.sharedPreferences.edit().apply {
            putString(Constants.AUTH_TOKEN, accessToken)
            apply()
        }
    }
}