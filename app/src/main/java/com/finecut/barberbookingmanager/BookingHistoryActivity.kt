package com.finecut.barberbookingmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.finecut.barberbookingmanager.adapters.BookingHistoryAdapter
import com.finecut.barberbookingmanager.databinding.ActivityBookingHistoryBinding
import com.finecut.barberbookingmanager.models.Bookings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingHistoryBinding
    private lateinit var bookingHistoryAdapter: BookingHistoryAdapter
    private val auth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val currentBarberId = auth.currentUser?.uid
        val bookingsReference = firebaseDatabase.getReference("Barbers")
            .child(currentBarberId!!).child("Bookings")

        bookingsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bookingsList = ArrayList<Bookings>()

                for (bookingSnapshot in dataSnapshot.children) {
                    val booking = bookingSnapshot.getValue(Bookings::class.java)
                    if (booking != null && booking.bookStatus == 2) {
                        bookingsList.add(booking)
                    }
                }

                bookingsList.sortWith { booking1, booking2 ->
                    val date1 = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(booking1.date)
                    val date2 = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(booking2.date)
                    when {
                        date1 == null -> 1
                        date2 == null -> -1
                        else -> date1.compareTo(date2)
                    }
                }

                bookingHistoryAdapter = BookingHistoryAdapter(this@BookingHistoryActivity, bookingsList)
                binding.rvBookingsHistory.layoutManager = LinearLayoutManager(this@BookingHistoryActivity)
                binding.rvBookingsHistory.adapter = bookingHistoryAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error: ", error.details)
            }
        })
    }
}