package com.finecut.barberbookingmanager.models

import android.os.Parcel
import android.os.Parcelable

data class Services(
    val name: String = "",
    val cost: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(cost)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Services> {
        override fun createFromParcel(parcel: Parcel): Services {
            return Services(parcel)
        }

        override fun newArray(size: Int): Array<Services?> {
            return arrayOfNulls(size)
        }
    }
}
