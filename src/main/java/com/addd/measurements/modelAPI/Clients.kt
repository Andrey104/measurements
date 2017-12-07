package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 07.12.2017.
 */
class Clients {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("client")
    @Expose
    val client: Client? = null

}