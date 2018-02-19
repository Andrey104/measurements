package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 19.02.18.
 */
class EventTransfer (
    @SerializedName("new_date")
    @Expose
    val newDate: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("old_date")
    @Expose
    val oldDate: String
)