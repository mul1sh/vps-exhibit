package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Person {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("id_number")
    @Expose
    lateinit var idNumber: String

    @SerializedName("name")
    @Expose
    lateinit var name: String

    @Expose
    lateinit var lastName: String

    @SerializedName("gender")
    @Expose
    lateinit var gender: String

    @SerializedName("phone_number")
    @Expose
    lateinit var phoneNumber: String

    @SerializedName("location_id")
    @Expose
    lateinit var locationId: String

    @SerializedName("sub_location_id")
    @Expose
    lateinit var subLocationId: String

    @SerializedName("postal_address")
    @Expose
    lateinit var postalAddress: String

    @SerializedName("town")
    @Expose
    lateinit var town: String

    @SerializedName("watchlist")
    @Expose
    lateinit var watchlist: String

    @SerializedName("reason")
    @Expose
    lateinit var reason: String

    @SerializedName("watchlist_photo")
    @Expose
    lateinit var watchlistPhoto: String

    @SerializedName("nationality")
    @Expose
    lateinit var nationality: String

    @SerializedName("wanted_for")
    @Expose
    lateinit var wantedFor: String

    @SerializedName("armed")
    @Expose
    lateinit var armed: String

    @SerializedName("bounty")
    @Expose
    lateinit var bounty: String

    @SerializedName("kin_name")
    @Expose
    internal var kinName: String = ""

    @SerializedName("kin_phone_number")
    @Expose
    internal var kinPhoneNumber: String = ""

    @SerializedName("kin_relation_id")
    @Expose
    internal var kinRelationId: String? = null

    @SerializedName("driving_licence_no")
    @Expose
    lateinit var drivingLicenceNo: String

    @SerializedName("cell_id")
    @Expose
    lateinit var cellId: String

    @SerializedName("station_name")
    @Expose
    lateinit var stationName: String

    @SerializedName("release_date")
    @Expose
    lateinit var releaseDate: String

    @SerializedName("mugshot_path")
    @Expose
    lateinit var mugshotPath: String

}