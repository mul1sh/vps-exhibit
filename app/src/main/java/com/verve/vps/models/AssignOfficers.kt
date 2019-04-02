package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssignOfficers {

    @SerializedName("officers")
    @Expose
    lateinit var officers: MutableList<Int>

}