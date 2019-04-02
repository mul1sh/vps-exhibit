package com.verve.vps.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SearchPersonResult {

    @SerializedName("result")
    @Expose
    var result: Boolean = false

    @SerializedName("message")
    @Expose
    lateinit var message: String

    @SerializedName("person")
    @Expose
    lateinit var person: Person

    @SerializedName("linked_occurrences")
    @Expose
    lateinit var linkedOccurrences: List<Any>

}