package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 11.01.2018.
 */
class Installers {
    @SerializedName("id")
    @Expose
    val id: Int? = null
    @SerializedName("installer")
    @Expose
    val installer: Installer? = null
}