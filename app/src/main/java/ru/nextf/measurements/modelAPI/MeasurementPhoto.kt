package ru.nextf.measurements.modelAPI

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by addd on 26.12.2017.
 */
class MeasurementPhoto : Parcelable {
    private var mUrl: String? = null
    private var idPhoto: Int? = null

    constructor(url: String, id: Int){
        mUrl = url
        idPhoto = id
    }

    constructor(inp: Parcel){
        mUrl = inp.readString()
        idPhoto = inp.readInt()
    }

    fun getUrl(): String? {
        return mUrl
    }

    fun setUrl(url: String) {
        mUrl = url
    }

    fun getId(): Int? {
        return idPhoto
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(mUrl)
        parcel.writeInt(idPhoto ?: -1)
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