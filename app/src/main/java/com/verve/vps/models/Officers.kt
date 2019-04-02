package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Officers {

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("serviceno")
    @Expose
    lateinit var serviceno: String

    @SerializedName("firstname")
    @Expose
    lateinit var firstname: String

    @SerializedName("lastname")
    @Expose
    lateinit var lastname: String

    @SerializedName("surname")
    @Expose
    lateinit var surname: String

    @SerializedName("id_no")
    @Expose
    lateinit var idNo: String

    @SerializedName("phone")
    @Expose
    lateinit var phone: String

    @SerializedName("profile_pic")
    @Expose
    lateinit var profilePic: String

    @SerializedName("gender")
    @Expose
    lateinit var gender: String

    @SerializedName("active")
    @Expose
    lateinit var active: String

    @SerializedName("role_id")
    @Expose
    lateinit var roleId: String

    @SerializedName("role")
    @Expose
    lateinit var role: String

    @SerializedName("station_id")
    @Expose
    lateinit var stationId: String

    @SerializedName("email")
    @Expose
    lateinit var email: String

    @SerializedName("category_id")
    @Expose
    lateinit var categoryId: String

    @SerializedName("category")
    @Expose
    lateinit var category: String

}