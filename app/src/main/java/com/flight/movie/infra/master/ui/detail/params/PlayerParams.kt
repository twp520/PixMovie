package com.flight.movie.infra.master.ui.detail.params

import android.os.Parcel
import android.os.Parcelable

/**
 * create by colin
 * 2024/7/17
 */
data class PlayerParams(
    val filmId: String,
    val filmType: String,
    val sNumber: Int = 0,
    val eNumber: Int = 0,
    val filmName: String,
    val vote: Float,
    val date: String,
    val cover: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerParams> {
        override fun createFromParcel(parcel: Parcel): PlayerParams {
            return PlayerParams(parcel)
        }

        override fun newArray(size: Int): Array<PlayerParams?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filmId)
        parcel.writeString(filmType)
        parcel.writeInt(sNumber)
        parcel.writeInt(eNumber)
        parcel.writeString(filmName)
        parcel.writeFloat(vote)
        parcel.writeString(date)
        parcel.writeString(cover)
    }
}
