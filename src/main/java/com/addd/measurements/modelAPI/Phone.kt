package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 08.12.2017.
 */
class Phone(
        @SerializedName("id")
        @Expose
        val id: Int? = null,
        @SerializedName("number")
        @Expose
        val number: String? = null,
        @SerializedName("comment")
        @Expose
        val comment: Any? = null
)