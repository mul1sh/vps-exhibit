package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LinkOccurrences {

    @SerializedName("related")
    @Expose
    lateinit var related: MutableList<Int>

}