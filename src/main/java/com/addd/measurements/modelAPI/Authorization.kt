package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 04.12.2017.
 */
class Authorization(
        @SerializedName("token")
        @Expose
        val token: String? = null
)