package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 28.12.2017.
 */
class Action {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("user")
    @Expose
    val user: User? = null
    @SerializedName("type")
    @Expose
    val type: Int? = null
    @SerializedName("cause")
    @Expose
    val cause: Int? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("comment")
    @Expose
    val comment: String? = null
    @SerializedName("action")
    @Expose
    val action: String? = null
    @SerializedName("deal")
    @Expose
    val deal: Int? = null
}