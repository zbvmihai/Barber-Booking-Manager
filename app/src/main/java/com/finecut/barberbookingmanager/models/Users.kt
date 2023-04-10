package com.finecut.barberbookingmanager.models

import android.os.Parcel
import android.os.Parcelable

// This is the data class model for the Users object.
data class Users(
    val id: String = "",
    val firstName: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val client: Int = 1,
    val image: String = "",
    val bookings: List<Bookings> = emptyList(),
    val points: Long = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createTypedArrayList(Bookings.CREATOR)!!,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(firstName)
        parcel.writeString(surname)
        parcel.writeString(email)
        parcel.writeString(phoneNumber)
        parcel.writeInt(client)
        parcel.writeString(image)
        parcel.writeTypedList(bookings)
        parcel.writeLong(points)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}
