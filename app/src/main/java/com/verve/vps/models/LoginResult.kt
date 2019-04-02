package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResult {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("access_token")
    @Expose
    lateinit var access_token: String

    @SerializedName("user_data")
    @Expose
    lateinit var userData: UserData

}