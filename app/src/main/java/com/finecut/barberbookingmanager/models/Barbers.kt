package com.finecut.barberbookingmanager.models
import android.os.Parcel
import android.os.Parcelable

data class Barbers(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val rating: Float = 0F,
    val description: String = "",
    val services: List<Services> = emptyList(),
    val bookings: List<Bookings> = emptyList(),
    val reviews: List<Reviews> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readString()!!,
        parcel.createTypedArrayList(Services.CREATOR)!!,
        parcel.createTypedArrayList(Bookings.CREATOR)!!,
        parcel.createTypedArrayList(Reviews.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeFloat(rating)
        parcel.writeString(description)
        parcel.writeTypedList(services)
        parcel.writeTypedList(bookings)
        parcel.writeTypedList(reviews)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Barbers> {
        override fun createFromParcel(parcel: Parcel): Barbers {
            return Barbers(parcel)
        }

        override fun newArray(size: Int): Array<Barbers?> {
            return arrayOfNulls(size)
        }
    }
}
