package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 07.12.2017.
 */
class Client {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("fio")
    @Expose
    val fio: String? = null
    @SerializedName("email")
    @Expose
    val email: Any? = null
}