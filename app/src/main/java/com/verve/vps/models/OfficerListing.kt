package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OfficersListing {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("officers")
    @Expose
    lateinit var officers: List<Officers>

}