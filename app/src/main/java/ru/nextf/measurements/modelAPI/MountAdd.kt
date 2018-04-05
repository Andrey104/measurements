package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by addd on 28.12.2017.
 */
class MountAdd (
    @SerializedName("date")
    @Expose
    val date: String?,
    @SerializedName("description")
    @Expose
    val description: String?
)