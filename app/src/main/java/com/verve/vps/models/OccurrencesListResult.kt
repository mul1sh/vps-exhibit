package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OccurrencesListResult {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("occurrences")
    @Expose
    lateinit var occurrences: MutableList<Occurrence>

}