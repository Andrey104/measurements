package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 28.03.18.
 */
class NewCommentMount (
        @SerializedName("mount_id")
        @Expose
        val id: Int,
        @SerializedName("comment")
        @Expose
        val comment: Comment
)