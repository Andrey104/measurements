package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 21.02.18.
 */
class EventCreate (
    @SerializedName("date")
    @Expose
    val date: String
)