package com.addd.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 24.12.2017.
 */
class CommentRequest(
        @SerializedName("text")
        @Expose
        val text: String
)