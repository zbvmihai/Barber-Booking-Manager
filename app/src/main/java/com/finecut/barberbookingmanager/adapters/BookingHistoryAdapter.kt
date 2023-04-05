package com.finecut.barberbookingmanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finecut.barberbookingmanager.R
import com.finecut.barberbookingmanager.databinding.HistoryCardBinding
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.models.Users
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class BookingHistoryAdapter(
    private val context: Context,
    private val bookingsList: ArrayList<Bookings>
) : RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder>() {

    inner class BookingHistoryViewHolder(val adapterBinding: HistoryCardBinding) :
        RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingHistoryViewHolder {
        val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingHistoryViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingHistoryViewHolder, position: Int) {
        val booking = bookingsList[holder.adapterPosition]

        FirebaseData.DBHelper.getCurrentUserFromDatabase(bookingsList[holder.adapterPosition].userId,
            object: FirebaseData.DBHelper.CurrentUserCallback{
                override fun onSuccess(currentUser: Users) {
                    holder.adapterBinding.tvClientName.text = "${currentUser.firstName} ${currentUser.surname}"

                    Picasso.get().load(currentUser.image.ifEmpty { context.getString(R.string.userImagePlaceHolder)})
                        .into(holder.adapterBinding.ivClientImage)
                }

                override fun onFailure(error: DatabaseError) {
                    Log.e("Database Error: ", error.toString())
                }
            })

       holder.adapterBinding.tvBookingDateTime.text = "${booking.date} at ${booking.timeslot}"
       holder.adapterBinding.tvBookingServiceName.text = booking.service
       holder.adapterBinding.tvBookingAmountPaid.text = booking.totalPaid
       holder.adapterBinding.tvBookingOffer.text = booking.offer

        when(booking.bookStatus){

            0 -> {holder.adapterBinding.tvStatus.text = "Booked"}
            1 -> {holder.adapterBinding.tvStatus.text = "Confirmed"}
            2 -> {holder.adapterBinding.tvStatus.text = "Completed"}
            else -> {holder.adapterBinding.tvStatus.text = "Unknown"}

        }
    }

    override fun getItemCount(): Int {
        return bookingsList.size
    }
}
