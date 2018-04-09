package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by left0ver on 09.04.18.
 */
class VoiceAdd (
    @SerializedName("comment_type")
    @Expose
    var commentType: Int?
)