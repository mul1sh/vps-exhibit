package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Exhibit {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("occurrence_id")
    @Expose
    lateinit var occurrenceId: String

    @SerializedName("type")
    @Expose
    lateinit var type: String

    @SerializedName("serial_number")
    @Expose
    lateinit var serialNumber: String

    @SerializedName("category_id")
    @Expose
    lateinit var categoryId: String

    @SerializedName("description")
    @Expose
    lateinit var description: String

    @SerializedName("quantity")
    @Expose
    lateinit var quantity: String

    @SerializedName("unit_id")
    @Expose
    lateinit var unitId: String

    @SerializedName("date_received")
    @Expose
    lateinit var dateReceived: String

    @SerializedName("date_disposed")
    @Expose
    var dateDisposed: String? = null

    @SerializedName("image_path")
    @Expose
    var imagePath: String? = null

    @SerializedName("station_id")
    @Expose
    lateinit var stationId: String

    @SerializedName("exhibit_category")
    @Expose
    lateinit var exhibitCategory: String

    @SerializedName("user_id")
    @Expose
    lateinit var userId: String

    @SerializedName("flag")
    @Expose
    lateinit var flag: String

    @SerializedName("base_path")
    @Expose
    lateinit var basePath: String

    @SerializedName("occurrence_no")
    @Expose
    lateinit var occurrenceNo: String

    @SerializedName("occurrence_description")
    @Expose
    lateinit var occurrenceDescription: String

    @SerializedName("category")
    @Expose
    lateinit var category: String

    @SerializedName("unit")
    @Expose
    lateinit var unit: String

    @SerializedName("user_name")
    @Expose
    lateinit var userName: String

}