package com.verve.vps.models

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class UserData {

    @SerializedName("iss")
    @Expose
    lateinit var iss: String

    @SerializedName("aud")
    @Expose
    lateinit var aud: String

    @SerializedName("iat")
    @Expose
    var iat: Int? = null

    @SerializedName("id")
    @Expose
    lateinit var id: String

    @SerializedName("firstname")
    @Expose
    lateinit var firstname: String

    @SerializedName("lastname")
    @Expose
    lateinit var lastname: String

    @SerializedName("surname")
    @Expose
    lateinit var surname: String

    @SerializedName("serviceno")
    @Expose
    lateinit var serviceno: String

    @SerializedName("role")
    @Expose
    lateinit var role: String

    @SerializedName("role_id")
    @Expose
    lateinit var roleId: String

    @SerializedName("gender")
    @Expose
    lateinit var gender: String

    @SerializedName("station")
    @Expose
    lateinit var station: String

    @SerializedName("station_id")
    @Expose
    lateinit var stationId: String

    @SerializedName("phone")
    @Expose
    lateinit var phone: String

    @SerializedName("id_no")
    @Expose
    lateinit var idNo: String

    @SerializedName("password_status")
    @Expose
    lateinit var passwordStatus: String

}