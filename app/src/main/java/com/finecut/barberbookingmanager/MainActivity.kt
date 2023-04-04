package com.finecut.barberbookingmanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.finecut.barberbookingmanager.adapters.BookingsAdapter
import com.finecut.barberbookingmanager.databinding.ActivityMainBinding
import com.finecut.barberbookingmanager.models.Barbers
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    private var auth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()


    private lateinit var bookingsAdapter: BookingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        val currentBarberId = auth.currentUser?.uid

        val bookingsReference =
            firebaseDatabase.getReference("Barbers").child(currentBarberId!!).child("Bookings")

        FirebaseData.DBHelper.getBarberFromDatabase(
            currentBarberId,
            object : FirebaseData.DBHelper.BarberCallback {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
                override fun onSuccess(barber: Barbers) {

                    mainBinding.BookingsTbTitle.text = "${barber.name} Upcoming Bookings"
                    mainBinding.rbBarberRating.rating = barber.rating

                    Picasso.get().load(barber.image.ifEmpty { getString(R.string.userImagePlaceHolder) })
                        .into(mainBinding.ivBarberImage, object :
                            Callback {
                            override fun onSuccess() {
                                mainBinding.pbBarberImage.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                if (e != null) {
                                    e.localizedMessage?.let { Log.e("Error Loading Image: ", it) }
                                }
                            }
                        })
                }

                override fun onFailure(error: DatabaseError) {
                    Log.e("Database Error: ", error.toString())
                }
            })

        bookingsReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bookingsList = ArrayList<Bookings>()

                for (bookingSnapshot in dataSnapshot.children) {
                    val booking = bookingSnapshot.getValue(Bookings::class.java)
                    if (booking != null) {
                        val bookingDateTime = parseDateTime(booking.date, booking.timeslot)
                        val currentDateTime = Calendar.getInstance().time
                        val currentDate = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time

                        Log.d("DEBUG", "Booking date: ${booking.date}, timeslot: ${booking.timeslot}")
                        Log.d("DEBUG", "Booking DateTime: $bookingDateTime")
                        Log.d("DEBUG", "Current DateTime: $currentDateTime")
                        Log.d("DEBUG", "Booking status: ${booking.bookStatus}")

                        if (bookingDateTime != null &&
                            (bookingDateTime > currentDateTime ||
                                    (bookingDateTime == currentDateTime && booking.date != SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(currentDate))) &&
                            (booking.bookStatus == 0 || booking.bookStatus == 1)) {
                            bookingsList.add(booking)
                        }
                    }
                }

                bookingsList.sortWith { booking1, booking2 ->
                    val dateTime1 = parseDateTime(booking1.date, booking1.timeslot)
                    val dateTime2 = parseDateTime(booking2.date, booking2.timeslot)
                    when {
                        dateTime1 == null -> 1
                        dateTime2 == null -> -1
                        else -> dateTime1.compareTo(dateTime2)
                    }
                }

                bookingsAdapter = BookingsAdapter(this@MainActivity, bookingsList)
                mainBinding.rvBookings.layoutManager = LinearLayoutManager(this@MainActivity)
                mainBinding.rvBookings.adapter = bookingsAdapter
                bookingsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.toString())
            }

            fun parseDateTime(date: String, timeSlot: String): Date? {
                val dateTime = "$date $timeSlot"
                val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                return try {
                    format.parse(dateTime)
                } catch (e: ParseException) {
                    e.printStackTrace()
                    null
                }
            }
        })
    }
}