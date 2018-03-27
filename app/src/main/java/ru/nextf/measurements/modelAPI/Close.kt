package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 17.12.2017.
 */
class Close(
        @SerializedName("comment")
        @Expose
        var comment: String?,
        @SerializedName("prepayment")
        @Expose
        var prepayment: Float?,
        @SerializedName("sum")
        @Expose
        var sum: Float?,
        @SerializedName("contract")
        @Expose
        var contract: Boolean,
        @SerializedName("mount_date")
        @Expose
        var mountDate: String?,
        @SerializedName("non_cash")
        @Expose
        var nonCash: Int?

)