package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 28.12.2017.
 */
class MyResultDeals {
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
    val results: List<Deal>? = null
}