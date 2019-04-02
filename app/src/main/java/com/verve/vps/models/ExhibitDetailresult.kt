package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExhibitDetailsResult {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("details")
    @Expose
    lateinit var details: Exhibit

    @SerializedName("activity")
    @Expose
    lateinit var activity: List<ExhibitActivity>

}