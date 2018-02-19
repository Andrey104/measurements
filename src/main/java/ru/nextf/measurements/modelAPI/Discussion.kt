package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 28.12.2017.
 */
class Discussion {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("date")
    @Expose
    val date: String? = null
    @SerializedName("status")
    @Expose
    val status: Int? = null
    @SerializedName("title")
    @Expose
    val title: String? = null
    @SerializedName("description")
    @Expose
    val description: String? = null
    @SerializedName("closed")
    @Expose
    val closed: Boolean? = null
    @SerializedName("user")
    @Expose
    val user: User? = null
}