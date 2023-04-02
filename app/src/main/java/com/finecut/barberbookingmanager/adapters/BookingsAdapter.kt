package com.finecut.barberbookingmanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finecut.barberbookingmanager.R
import com.finecut.barberbookingmanager.databinding.BookingsCardBinding
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.models.Users
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingsAdapter(private var context: Context,
    private var bookingsList: ArrayList<Bookings>)
    : RecyclerView.Adapter<BookingsAdapter.BookingsViewHolder>() {

    inner class BookingsViewHolder(val adapterBinding: BookingsCardBinding)
        : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsViewHolder {
        val binding = BookingsCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookingsViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingsViewHolder, position: Int) {

        FirebaseData.DBHelper.getCurrentUserFromDatabase(bookingsList[holder.adapterPosition].userId,
            object : FirebaseData.DBHelper.CurrentUserCallback{
                @SuppressLint("SetTextI18n")
                override fun onSuccess(currentUser: Users) {

                    holder.adapterBinding.tvClientName.text = "${currentUser.firstName} ${currentUser.surname}"

                    Picasso.get().load(currentUser.image.ifEmpty {context.getString(R.string.userImagePlaceHolder)})
                        .into(holder.adapterBinding.ivClientImage)

                    holder.adapterBinding.pbBookingCard.visibility = View.GONE
                    holder.adapterBinding.clBookingCard.visibility = View.VISIBLE
                }
                override fun onFailure(error: DatabaseError) {
                    Log.e("Database Error: ", error.details)
                }
            })

        val storedDate = bookingsList[holder.adapterPosition].date
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        if (storedDate == currentDate) {
            holder.adapterBinding.tvBookingDateTime.text = "Today at " +
                    bookingsList[holder.adapterPosition].timeslot
        } else {
            holder.adapterBinding.tvBookingDateTime.text = "${bookingsList[holder.adapterPosition].date} at " +
                    bookingsList[holder.adapterPosition].timeslot
        }

        holder.adapterBinding.tvBookingServiceName.text = bookingsList[holder.adapterPosition].service
        holder.adapterBinding.tvBookingAmountPaid.text = bookingsList[holder.adapterPosition].totalPaid
        holder.adapterBinding.tvBookingOffer.text = bookingsList[holder.adapterPosition].offer.ifEmpty { "No Offer/Discount" }

        holder.adapterBinding.ibAccept.setOnClickListener {

            val booking = bookingsList[holder.adapterPosition]

        }

        holder.adapterBinding.ibDecline.setOnClickListener {

            val booking = bookingsList[holder.adapterPosition]

        }

    }

    override fun getItemCount(): Int {
        return bookingsList.size
    }
}