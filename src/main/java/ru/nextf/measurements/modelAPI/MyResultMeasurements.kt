package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 18.12.2017.
 */
class MyResultMeasurements(
        @SerializedName("count")
        @Expose
        val count: Int? = null,
        @SerializedName("not_distributed")
        @Expose
        val notDistributed: Int? = null,
        @SerializedName("my_measurements")
        @Expose
        val myMeasurements: Int? = null,
        @SerializedName("next")
        @Expose
        val next: String? = null,
        @SerializedName("previous")
        @Expose
        val previous: String? = null,
        @SerializedName("results")
        @Expose
        val results: List<Measurement>? = null
)