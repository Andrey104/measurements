package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 10.04.18.
 */
class MeasurementNote(
        @SerializedName("note")
        @Expose
        val note: String)
