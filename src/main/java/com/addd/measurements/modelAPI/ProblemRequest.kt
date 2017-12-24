package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 20.12.2017.
 */
class ProblemRequest(
        @SerializedName("title")
        @Expose
        var title: String,
        @SerializedName("description")
        @Expose
        var description: String
)