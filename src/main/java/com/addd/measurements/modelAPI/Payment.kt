package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 28.12.2017.
 */
class Payment {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("user")
    @Expose
    val user: String? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("date")
    @Expose
    val date: String? = null
    @SerializedName("sum")
    @Expose
    val sum: String? = null
    @SerializedName("type")
    @Expose
    val type: Int? = null
    @SerializedName("receiver")
    @Expose
    val receiver: String? = null
    @SerializedName("comment")
    @Expose
    val comment: Any? = null
    @SerializedName("deal")
    @Expose
    val deal: Int? = null
}