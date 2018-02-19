package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 19.02.18.
 */
class Event {
    @SerializedName("event")
    @Expose
    val event: String? = null
    @SerializedName("data")
    @Expose
    val data: Any? = null
}