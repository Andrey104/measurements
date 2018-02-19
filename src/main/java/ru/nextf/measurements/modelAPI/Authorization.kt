package ru.nextf.measurements.modelAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by addd on 04.12.2017.
 */
class Authorization {
        @SerializedName("first_name")
        @Expose
        val firstName: String? = null
        @SerializedName("phone")
        @Expose
        val phone: String? = null
        @SerializedName("id")
        @Expose
        val id: Int? = null
        @SerializedName("telegram")
        @Expose
        val telegram: String? = null
        @SerializedName("token")
        @Expose
        val token: String? = null
        @SerializedName("username")
        @Expose
        val username: String? = null
        @SerializedName("last_name")
        @Expose
        val lastName: String? = null
        @SerializedName("type")
        @Expose
        val type: Int? = null
        @SerializedName("email")
        @Expose
        val email: String? = null
}