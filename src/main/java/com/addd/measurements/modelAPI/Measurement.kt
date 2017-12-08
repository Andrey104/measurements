package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 07.12.2017.
 */
class Measurement {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("date")
    @Expose
    val date: String? = null
    @SerializedName("status")
    @Expose
    val status: Int? = null
    @SerializedName("manager_comment")
    @Expose
    val managerComment: Any? = null
    @SerializedName("comment")
    @Expose
    val comment: Any? = null
    @SerializedName("time")
    @Expose
    val time: String? = null
    @SerializedName("sum")
    @Expose
    val sum: Any? = null
    @SerializedName("prepayment")
    @Expose
    val prepayment: Any? = null
    @SerializedName("worker")
    @Expose
    val worker: Any? = null
    @SerializedName("deal")
    @Expose
    val deal: Int? = null
    @SerializedName("pictures")
    @Expose
    val pictures: List<Any>? = null
    @SerializedName("color")
    @Expose
    val color: Int? = null
    @SerializedName("worker_name")
    @Expose
    val workerName: Any? = null
    @SerializedName("deal_comment")
    @Expose
    val dealComment: Any? = null
    @SerializedName("address")
    @Expose
    val address: String? = null
    @SerializedName("clients")
    @Expose
    val clients: List<Clients>? = null
    @SerializedName("actions")
    @Expose
    val actions: List<Any>? = null
    @SerializedName("company")
    @Expose
    val company: Company? = null
}