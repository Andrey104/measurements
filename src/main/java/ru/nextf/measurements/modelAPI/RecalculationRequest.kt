package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 10.01.2018.
 */
class RecalculationRequest(
        @SerializedName("after")
        @Expose
        var after: Float,
        @SerializedName("comment")
        @Expose
        var comment: String
)