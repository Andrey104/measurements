package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 16.12.2017.
 */
class Reject(@SerializedName("cause")
             @Expose
             var cause: Int?,
             @SerializedName("comment")
             @Expose
             var comment: String?)