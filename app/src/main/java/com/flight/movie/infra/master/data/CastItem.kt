package com.flight.movie.infra.master.data

import android.os.Parcel
import android.os.Parcelable
import com.flight.movie.infra.master.ui.MULTI_TYPE_DATA
import com.flight.movie.infra.master.ui.state.MultipleItemState
import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/8
 */
data class CastItem(
    val id: String,
    val gender: Int,
    val name: String,
    @SerializedName("profile_path")
    val profile: String,
    val character: String,
    val job: String? = ""
) : Parcelable, MultipleItemState {

    override val type: Int
        get() = MULTI_TYPE_DATA

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(gender)
        parcel.writeString(name)
        parcel.writeString(profile)
        parcel.writeString(character)
        parcel.writeString(job)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CastItem> {
        override fun createFromParcel(parcel: Parcel): CastItem {
            return CastItem(parcel)
        }

        override fun newArray(size: Int): Array<CastItem?> {
            return arrayOfNulls(size)
        }
    }

}