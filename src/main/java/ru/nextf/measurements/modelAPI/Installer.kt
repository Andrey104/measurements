package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 11.01.2018.
 */
class Installer {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("fio")
    @Expose
    val fio: String? = null
    @SerializedName("phone")
    @Expose
    val phone: String? = null
}