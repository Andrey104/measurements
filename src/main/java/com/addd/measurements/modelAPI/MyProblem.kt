package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 22.12.2017.
 */
class MyProblem(
        @SerializedName("id")
        @Expose
        val id: Int,
        @SerializedName("address")
        @Expose
        val address: String?,
        @SerializedName("user")
        @Expose
        val user: User,
        @SerializedName("date")
        @Expose
        val date: String,
        @SerializedName("status")
        @Expose
        val status: Int,
        @SerializedName("title")
        @Expose
        val title: String,
        @SerializedName("description")
        @Expose
        val description: String,
        @SerializedName("deal")
        @Expose
        val deal: Int,
        @SerializedName("comments")
        @Expose
        val comments: List<Comment>? = null
)