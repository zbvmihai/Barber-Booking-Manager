package com.finecut.barberbookingmanager.models

import android.os.Parcel
import android.os.Parcelable

data class Offers(
    val date: String = "",
    val description: String = "",
    val code: String = "",
    val deduction: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(description)
        parcel.writeString(code)
        parcel.writeInt(deduction)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Offers> {
        override fun createFromParcel(parcel: Parcel): Offers {
            return Offers(parcel)
        }

        override fun newArray(size: Int): Array<Offers?> {
            return arrayOfNulls(size)
        }
    }
}