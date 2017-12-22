package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 22.12.2017.
 */
class MyResultProblem (
        @SerializedName("count")
        @Expose
        val count: Int? = null,
        @SerializedName("next")
        @Expose
        val next: String? = null,
        @SerializedName("previous")
        @Expose
        val previous: String? = null,
        @SerializedName("results")
        @Expose
        val results: List<MyProblem>? = null
)