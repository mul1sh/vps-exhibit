package com.verve.vps.models

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OccurrenceReportDetailsResult {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("details")
    @Expose
    lateinit var occurrenceReport: OccurrenceReport

    @SerializedName("reporter")
    @Expose
    lateinit var occurrenceReporterDetails: OccurrenceReporterDetails

    @SerializedName("related_occurrences")
    @Expose
    lateinit var relatedOccurrences: JsonArray

    @SerializedName("exhibits")
    @Expose
    lateinit var exhibits: Any

}