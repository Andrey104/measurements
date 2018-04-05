package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 26.12.2017.
 */
class Picture {
    @SerializedName("id")
    @Expose
    val id: Int = -1
    @SerializedName("user")
    @Expose
    val user: User? = null
    @SerializedName("auto_date")
    @Expose
    val autoDate: String? = null
    @SerializedName("url")
    @Expose
    val url: String? = null
    @SerializedName("name")
    @Expose
    val name: String? = null
    @SerializedName("measurement")
    @Expose
    val measurement: Int? = null
}