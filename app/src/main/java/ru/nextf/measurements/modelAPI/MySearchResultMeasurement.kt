package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 12.01.2018.
 */
class MySearchResultMeasurement {
    @SerializedName("count")
    @Expose
    val count: Int? = null
    @SerializedName("next")
    @Expose
    val next: String? = null
    @SerializedName("previous")
    @Expose
    val previous: String? = null
    @SerializedName("results")
    @Expose
    val results: List<Measurement>? = null
}