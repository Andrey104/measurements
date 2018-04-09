package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 22.12.2017.
 */
class Comment(
        @SerializedName("id")
        @Expose
        val id: Int,
        @SerializedName("user")
        @Expose
        val user: User,
        @SerializedName("auto_date")
        @Expose
        val date: String,
        @SerializedName("text")
        @Expose
        val text: String,
        @SerializedName("read")
        @Expose
        val read: Boolean,
        @SerializedName("comment_type")
        @Expose
        val commentType: Int?,
        @SerializedName("file")
        @Expose
        val file: String?
)