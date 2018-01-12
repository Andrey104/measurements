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
    @SerializedName("actions")
    @Expose
    val actions: List<Action>? = null
    @SerializedName("stages")
    @Expose
    val stages: List<Stage>? = null
    @SerializedName("company")
    @Expose
    val company: Company? = null
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
    val comment: String? = null
    @SerializedName("deal")
    @Expose
    val deal: Int? = null
}