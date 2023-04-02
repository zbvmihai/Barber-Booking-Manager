package com.finecut.barberbookingmanager.models

import android.os.Parcel
import android.os.Parcelable

data class Reviews(
    val userId: String = "",
    val rating: Float = 0F,
    val comment: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeFloat(rating)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reviews> {
        override fun createFromParcel(parcel: Parcel): Reviews {
            return Reviews(parcel)
        }

        override fun newArray(size: Int): Array<Reviews?> {
            return arrayOfNulls(size)
        }
    }
}
