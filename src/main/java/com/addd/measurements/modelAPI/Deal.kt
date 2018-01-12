package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 25.12.2017.
 */
class Deal {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("sum")
    @Expose
    val sum: Float? = null
    @SerializedName("status")
    @Expose
    val status: Int? = null
    @SerializedName("address_comment")
    @Expose
    val addressComment: String? = null
    @SerializedName("address")
    @Expose
    val address: String? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("contract")
    @Expose
    val contract: Boolean? = null
    @SerializedName("task_date")
    @Expose
    val taskDate: String? = null
    @SerializedName("order")
    @Expose
    val order: Any? = null
    @SerializedName("user")
    @Expose
    val user: User? = null
    @SerializedName("company")
    @Expose
    val company: Company? = null
    @SerializedName("worker")
    @Expose
    val worker: User? = null
    @SerializedName("clients")
    @Expose
    val clients: List<Clients>? = null
    @SerializedName("actions")
    @Expose
    val actions: List<Action>? = null
    @SerializedName("discussions")
    @Expose
    val discussions: List<Discussion>? = null
    @SerializedName("measurements")
    @Expose
    val measurements: List<Measurement>? = null
    @SerializedName("payments")
    @Expose
    val payments: List<Any>? = null
    @SerializedName("discounts")
    @Expose
    val discounts: List<Recalculation>? = null
    @SerializedName("mounts")
    @Expose
    val mounts: List<Any>? = null
    @SerializedName("comments")
    @Expose
    val comments: List<Any>? = null
}