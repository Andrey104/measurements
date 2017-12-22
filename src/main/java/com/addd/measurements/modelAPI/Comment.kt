package com.addd.measurements.modelAPI

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
        val user: String,
        @SerializedName("date")
        @Expose
        val date: String,
        @SerializedName("text")
        @Expose
        val text: String,
        @SerializedName("discussion")
        @Expose
        val discussion: Int
)