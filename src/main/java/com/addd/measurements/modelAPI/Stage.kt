package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 11.01.2018.
 */
class Stage {

    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("installers")
    @Expose
    val installers: List<Installers>? = null
    @SerializedName("actions")
    @Expose
    val actions: List<Action>? = null
    @SerializedName("transfers")
    @Expose
    val transfers: List<Transfer>? = null
    @SerializedName("costs")
    @Expose
    val costs: List<Any>? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("date")
    @Expose
    val date: String? = null
    @SerializedName("status")
    @Expose
    val status: Int? = null
    @SerializedName("comment")
    @Expose
    val comment: String? = null
    @SerializedName("mount")
    @Expose
    val mount: Int? = null
}