package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 05.04.18.
 */
class MountEdit (
        @SerializedName("date")
        @Expose
        val date: String?,
        @SerializedName("description")
        @Expose
        val description: String?,
        @SerializedName("cause")
        @Expose
        val cause: Int?

)