package com.flight.movie.infra.master.data

import android.os.Parcel
import android.os.Parcelable

/**
 * create by colin
 * 2024/7/4
 *
 * todo cache to room
 */
class Genres() : Parcelable {
    var id: Int = 0
    var name: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Genres> {
        override fun createFromParcel(parcel: Parcel): Genres {
            return Genres(parcel)
        }

        override fun newArray(size: Int): Array<Genres?> {
            return arrayOfNulls(size)
        }
    }
}
