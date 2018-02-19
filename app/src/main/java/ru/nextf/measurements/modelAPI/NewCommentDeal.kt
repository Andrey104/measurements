package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 19.02.18.
 */
class NewCommentDeal (
    @SerializedName("deal_id")
    @Expose
    val id: Int,
    @SerializedName("comment")
    @Expose
    val comment: Comment)