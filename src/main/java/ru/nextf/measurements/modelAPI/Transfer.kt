package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 15.12.2017.
 */
class Transfer (
        @SerializedName("new_date")
        @Expose
        var newDate: String?,
        @SerializedName("cause")
        @Expose
        var cause: Int?,
        @SerializedName("comment")
        @Expose
        var comment: String?
)