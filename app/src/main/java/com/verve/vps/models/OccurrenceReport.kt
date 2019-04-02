package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OccurrenceReport {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("occurrence_id")
    @Expose
    lateinit var occurrenceId: String

    @SerializedName("description")
    @Expose
    lateinit var description: String

    @SerializedName("area")
    @Expose
    lateinit var area: String

    @SerializedName("longitude")
    @Expose
    lateinit var longitude: String

    @SerializedName("latitude")
    @Expose
    lateinit var latitude: String

    @SerializedName("narrative")
    @Expose
    lateinit var narrative: String

    @SerializedName("reporter_id_number")
    @Expose
    lateinit var reporterIdNumber: String

    @SerializedName("occurrence_no")
    @Expose
    lateinit var occurrenceNo: String

    @SerializedName("status")
    @Expose
    lateinit var status: String

    @SerializedName("county_id")
    @Expose
    lateinit var countyId: String

    @SerializedName("county")
    @Expose
    lateinit var county: String

    @SerializedName("constituency_id")
    @Expose
    lateinit var constituencyId: String

    @SerializedName("constituency")
    @Expose
    lateinit var constituency: String

}