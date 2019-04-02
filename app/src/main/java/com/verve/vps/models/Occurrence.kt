package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Occurrence {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("occurrence_no")
    @Expose
    lateinit var occurrenceNo: String

    @SerializedName("description")
    @Expose
    lateinit var description: String

    @SerializedName("type_id")
    @Expose
    lateinit var typeId: String

    @SerializedName("date_created")
    @Expose
    lateinit var dateCreated: String

    @SerializedName("date_modified")
    @Expose
    lateinit var dateModified: String

    @SerializedName("posted_on")
    @Expose
    lateinit var postedOn: String

    @SerializedName("user_id")
    @Expose
    lateinit var userId: String

    @SerializedName("station_id")
    @Expose
    lateinit var stationId: String

    @SerializedName("county_id")
    @Expose
    lateinit var countyId: String

    @SerializedName("status")
    @Expose
    lateinit var status: String

    @SerializedName("station")
    @Expose
    lateinit var station: String

    @SerializedName("parent_id")
    @Expose
    lateinit var parent_id: String

    @SerializedName("child_id")
    @Expose
    lateinit var child_id: String

    @SerializedName("type")
    @Expose
    lateinit var occurrenceType: String

    @SerializedName("accomplice_count")
    @Expose
    lateinit var accompliceCount: String

    @SerializedName("personal")
    @Expose
    lateinit var personal: String

    @SerializedName("mugshot")
    @Expose
    lateinit var mugshot: String

    @SerializedName("fingerprint")
    @Expose
    lateinit var fingerprint: String

    @SerializedName("cell")
    @Expose
    lateinit var cell: String

}