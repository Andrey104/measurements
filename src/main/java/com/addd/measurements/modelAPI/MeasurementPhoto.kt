package com.addd.measurements.modelAPI

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by addd on 26.12.2017.
 */
class MeasurementPhoto : Parcelable {
    private var mUrl: String? = null
    private var mTitle: String? = null

    constructor(url: String, title: String){
        mUrl = url
        mTitle = title
    }

    constructor(inp: Parcel){
        mUrl = inp.readString()
        mTitle = inp.readString()
    }

    fun getUrl(): String? {
        return mUrl
    }

    fun setUrl(url: String) {
        mUrl = url
    }

    fun getTitle(): String? {
        return mTitle
    }

    fun setTitle(title: String) {
        mTitle = title
    }



    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(mUrl)
        parcel.writeString(mTitle)
    }

    companion object CREATOR : Parcelable.Creator<MeasurementPhoto> {
        override fun createFromParcel(parcel: Parcel): MeasurementPhoto {
            return MeasurementPhoto(parcel)
        }

        override fun newArray(size: Int): Array<MeasurementPhoto?> {
            return arrayOfNulls(size)
        }
    }
}