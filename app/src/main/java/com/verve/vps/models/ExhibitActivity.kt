package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExhibitActivity {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("exhibit_id")
    @Expose
    lateinit var exhibitId: String

    @SerializedName("category")
    @Expose
    lateinit var category: String

    @SerializedName("description")
    @Expose
    lateinit var description: String

    @SerializedName("destination")
    @Expose
    lateinit var destination: String

    @SerializedName("user_id")
    @Expose
    lateinit var userId: String

    @SerializedName("date_created")
    @Expose
    lateinit var dateCreated: String

}