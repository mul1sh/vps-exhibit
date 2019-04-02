package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OccurrenceReporterDetails {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("id_number")
    @Expose
    lateinit var idNumber: String

    @SerializedName("name")
    @Expose
    lateinit var name: String

    @SerializedName("gender")
    @Expose
    lateinit var gender: String

    @SerializedName("phone_number")
    @Expose
    lateinit var phoneNumber: String

    @SerializedName("location_id")
    @Expose
    lateinit var locationId: String

}