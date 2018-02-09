package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 10.01.2018.
 */
class Recalculation {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("before")
    @Expose
    val before: String? = null
    @SerializedName("after")
    @Expose
    val after: String? = null
    @SerializedName("comment")
    @Expose
    val comment: String? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("user")
    @Expose
    val user: User? = null
}