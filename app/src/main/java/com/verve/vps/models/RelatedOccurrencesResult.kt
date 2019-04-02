package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RelatedOccurrencesResult {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("related")
    @Expose
    lateinit var related: MutableList<Occurrence>

}