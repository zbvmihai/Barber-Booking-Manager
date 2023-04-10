package com.finecut.barberbookingmanager.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finecut.barberbookingmanager.BookingDetailsActivity
import com.finecut.barberbookingmanager.R
import com.finecut.barberbookingmanager.databinding.BookingsCardBinding
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.models.Users
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// This bookings adapter class take a list of bookings and populate the views of the bookings card
// in the recycler view of the My Bookings Activity.
class BookingsAdapter(private var context: Context,
    private var bookingsList: ArrayList<Bookings>)
    : RecyclerView.Adapter<BookingsAdapter.BookingsViewHolder>() {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var currentUser: Users

    inner class BookingsViewHolder(val adapterBinding: BookingsCardBinding)
        : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsViewHolder {
        val binding = BookingsCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookingsViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingsViewHolder, position: Int) {

        val booking = bookingsList[holder.adapterPosition]

        val bookingDate = "${booking.date}-${booking.timeslot.replace(":", "-")}"

        val userBookingRef: DatabaseReference = firebaseDatabase.getReference("Users")
            .child(bookingsList[holder.adapterPosition].userId)
            .child("Bookings").child(bookingDate)

        val barberBookingRef: DatabaseReference = firebaseDatabase.getReference("Barbers")
            .child(bookingsList[holder.adapterPosition].barberId)
            .child("Bookings").child(bookingDate)

        // This block of code retrieve the authenticated barber from the database
        // and populate the views Main activity.
        FirebaseData.DBHelper.getCurrentUserFromDatabase(bookingsList[holder.adapterPosition].userId,
            object : FirebaseData.DBHelper.CurrentUserCallback{
                @SuppressLint("SetTextI18n")
                override fun onSuccess(currentUser: Users) {
                    this@BookingsAdapter.currentUser = currentUser

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

        // Next lines of code populate the booking card with the booking data passed to the adapter
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

        // Next lines of code display the bookings status view based on booking status
        when(bookingsList[holder.adapterPosition].bookStatus){

            0 -> {
                holder.adapterBinding.tvStatus.text = "Booked"
            }
            1 -> {
                holder.adapterBinding.ibAccept.visibility = View.INVISIBLE
                holder.adapterBinding.tvStatus.text = "Confirmed"
                holder.adapterBinding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
            else -> {
                holder.adapterBinding.tvStatus.text = "Status"
            }
        }

        // If the accept button is clicked, the booking status will change accordingly
        holder.adapterBinding.ibAccept.setOnClickListener {

            userBookingRef.child("bookStatus").setValue(1).addOnSuccessListener {
                Toast.makeText(context.applicationContext,"Booking is confirmed!",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e("Database Error:", it1) }
            }
            barberBookingRef.child("bookStatus").setValue(1)
        }

        // If the decline button is clicked, an alert dialog will pop out and
        // if the barber click the yes button, the booking will be deleted from database.
        holder.adapterBinding.ibDecline.setOnClickListener {

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Decline Booking")
            alertDialogBuilder.setMessage("Are you sure you want to decline this booking?")

            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                userBookingRef.removeValue().addOnSuccessListener {
                    Toast.makeText(
                        context.applicationContext,
                        "Booking declined!",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    it.localizedMessage?.let { it2 -> Log.e("Database Error:", it2) }
                }

                barberBookingRef.removeValue()
            }
            alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        holder.adapterBinding.llBookingsCard.setOnClickListener {
            val intent = Intent(context, BookingDetailsActivity::class.java)
            intent.putExtra("booking",booking)
            intent.putExtra("currentUser",currentUser)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookingsList.size
    }
}