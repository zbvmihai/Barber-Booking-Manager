package com.finecut.barberbookingmanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.finecut.barberbookingmanager.databinding.ActivityBookingDetailsBinding
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.models.Users
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

@Suppress("DEPRECATION")
class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var bindingBookingDetails: ActivityBookingDetailsBinding
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private lateinit var booking: Bookings
    private lateinit var user: Users

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingBookingDetails = ActivityBookingDetailsBinding.inflate(layoutInflater)
        val view = bindingBookingDetails.root
        setContentView(view)

        booking = intent.getParcelableExtra("booking")!!
        user = intent.getParcelableExtra("currentUser")!!

        val bookingDate = "${booking.date}-${booking.timeslot.replace(":", "-")}"

        val userBookingRef: DatabaseReference = firebaseDatabase.getReference("Users")
            .child(booking.userId)
            .child("Bookings").child(bookingDate)

        val barberBookingRef: DatabaseReference = firebaseDatabase.getReference("Barbers")
            .child(booking.barberId)
            .child("Bookings").child(bookingDate)

        bindingBookingDetails.tvDetailsUserName.text = "${user.firstName} ${user.surname}"
        bindingBookingDetails.tvPhoneNr.text = user.phoneNumber
        bindingBookingDetails.tvEmail.text = user.email
        bindingBookingDetails.tvDate.text = booking.date
        bindingBookingDetails.tvTimeSlot.text = booking.timeslot

        when(booking.bookStatus){
            0 -> {bindingBookingDetails.tvBookingStatus.text = "Booked"}
            1 -> {
                bindingBookingDetails.tvBookingStatus.text = "Confirmed"
                bindingBookingDetails.ibMyProfileConfirm.visibility = View.GONE
                bindingBookingDetails.tvConfirmText.visibility = View.GONE
            }
            2 -> {bindingBookingDetails.tvBookingStatus.text = "Completed"}
            else -> {bindingBookingDetails.tvBookingStatus.text = "Unknown"}
        }

        Picasso.get().load(user.image.ifEmpty {getString(R.string.userImagePlaceHolder)})
            .into(bindingBookingDetails.ivDetailsUserImage, object : Callback{
                override fun onSuccess() {
                    bindingBookingDetails.pbDetailsUserImage.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    if (e != null) {
                        e.localizedMessage?.let { Log.e("Error Loading Image: ", it) }
                    }
                }
            })

        bindingBookingDetails.ibMyProfileConfirm.setOnClickListener {


            userBookingRef.child("bookStatus").setValue(1).addOnSuccessListener {
                Toast.makeText(applicationContext,"Booking is confirmed!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e("Database Error:", it1) }
            }
            barberBookingRef.child("bookStatus").setValue(1)
            bindingBookingDetails.tvBookingStatus.text = "Confirmed"
            bindingBookingDetails.ibMyProfileConfirm.visibility = View.GONE
            bindingBookingDetails.tvConfirmText.visibility = View.GONE
        }

        bindingBookingDetails.ibMyProfileDecline.setOnClickListener {

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Decline Booking")
            alertDialogBuilder.setMessage("Are you sure you want to decline this booking?")

            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                userBookingRef.removeValue().addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Booking declined!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
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

        bindingBookingDetails.ibCallCustomer.setOnClickListener {

            val phoneNumber = user.phoneNumber
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }

        bindingBookingDetails.btnComplete.setOnClickListener {

            userBookingRef.child("bookStatus").setValue(2).addOnSuccessListener {
                Toast.makeText(applicationContext,"Booking is completed!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.e("Database Error:", it1) }
            }
            barberBookingRef.child("bookStatus").setValue(2)
            finish()
        }
    }
}