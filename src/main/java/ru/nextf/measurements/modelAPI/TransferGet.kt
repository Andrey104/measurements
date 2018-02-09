package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by addd on 28.12.2017.
 */
class TransferGet {
    @SerializedName("id")
    @Expose
     val id: Int? = null
    @SerializedName("cause")
    @Expose
     val cause: Int? = null
    @SerializedName("new_date")
    @Expose
     val newDate: String? = null
    @SerializedName("old_date")
    @Expose
     val oldDate: String? = null
    @SerializedName("auto_date")
    @Expose
     val autoDate: String? = null
    @SerializedName("comment")
    @Expose
     val comment: String? = null
    @SerializedName("user")
    @Expose
     val user: User? = null
}