package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExhibitListResult {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("exhibits")
    @Expose
    lateinit var exhibits: MutableList<Exhibit>


}