package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 07.12.2017.
 */
class Measurement(
        @SerializedName("id")
        @Expose
        val id: Int? = null,
        @SerializedName("auto_date")
        @Expose
        val autoDate: String? = null,
        @SerializedName("date")
        @Expose
        val date: String? = null,
        @SerializedName("status")
        @Expose
        val status: Int? = null,
        @SerializedName("comment")
        @Expose
        val comment: Any? = null,
        @SerializedName("time")
        @Expose
        val time: String? = null,
        @SerializedName("sum")
        @Expose
        val sum: Float? = null,
        @SerializedName("prepayment")
        @Expose
        val prepayment: Float? = null,
        @SerializedName("worker")
        @Expose
        val worker: User? = null,
        @SerializedName("deal")
        @Expose
        val deal: Int? = null,
        @SerializedName("pictures")
        @Expose
        val pictures: List<Picture>? = null,
        @SerializedName("color")
        @Expose
        val color: Int? = null,
        @SerializedName("address_comment")
        @Expose
        val addressComment: String? = null,
        @SerializedName("address")
        @Expose
        val address: String? = null,
        @SerializedName("clients")
        @Expose
        val clients: List<Clients>? = null,
        @SerializedName("actions")
        @Expose
        val actions: List<Any>? = null,
        @SerializedName("comments")
        @Expose
        val comments: List<Comment>? = null,
        @SerializedName("company")
        @Expose
        val company: Company? = null,
        @SerializedName("non_cash")
        @Expose
        val nonCash: Boolean? = null,
        @SerializedName("transfers")
        @Expose
        val transfers: List<TransferGet>? = null
)