package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 28.12.2017.
 */
class Mount {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("date_mount")
    @Expose
    val dateMount: String? = null
    @SerializedName("date")
    @Expose
    val date: String? = null
    @SerializedName("status")
    @Expose
    val status: Int? = null
    @SerializedName("comment")
    @Expose
    val comment: Any? = null
    @SerializedName("deal")
    @Expose
    val deal: Int? = null
}