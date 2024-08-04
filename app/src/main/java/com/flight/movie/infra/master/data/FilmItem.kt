package com.flight.movie.infra.master.data

import android.os.Parcel
import android.os.Parcelable
import com.flight.movie.infra.master.ui.MULTI_TYPE_DATA
import com.flight.movie.infra.master.ui.state.MultipleItemState
import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/2
 */

data class FilmItem(
    val id: String,
    val name: String? = "",
    val title: String? = "",
    @SerializedName("poster_path")
    val poster: String? = "",
    @SerializedName("media_type")
    val mediaType: String? = "",
    @SerializedName("vote_average")
    val vote: Float
) : Parcelable, MultipleItemState {

    override val type: Int
        get() = MULTI_TYPE_DATA

    val displayName: String
        get() = name ?: title ?: ""

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(title)
        parcel.writeString(poster)
        parcel.writeString(mediaType)
        parcel.writeFloat(vote)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilmItem> {
        override fun createFromParcel(parcel: Parcel): FilmItem {
            return FilmItem(parcel)
        }

        override fun newArray(size: Int): Array<FilmItem?> {
            return arrayOfNulls(size)
        }
    }

}
