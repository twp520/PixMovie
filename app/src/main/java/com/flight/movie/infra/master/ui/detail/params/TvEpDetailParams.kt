package com.flight.movie.infra.master.ui.detail.params

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

/**
 * create by colin
 * 2024/7/10
 */
@Entity(primaryKeys = ["tvId", "seNumber"])
data class TvEpDetailParams(
    val tvId: String,
    val seNumber: Int,
    val name: String,
    val tags: String,
    val country: String,
    val tvPoster: String,
    val tvRate: Float
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tvId)
        parcel.writeInt(seNumber)
        parcel.writeString(name)
        parcel.writeString(tags)
        parcel.writeString(country)
        parcel.writeString(tvPoster)
        parcel.writeFloat(tvRate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TvEpDetailParams> {
        override fun createFromParcel(parcel: Parcel): TvEpDetailParams {
            return TvEpDetailParams(parcel)
        }

        override fun newArray(size: Int): Array<TvEpDetailParams?> {
            return arrayOfNulls(size)
        }
    }

}
