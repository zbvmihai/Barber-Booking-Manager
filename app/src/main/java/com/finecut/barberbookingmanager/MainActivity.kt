package com.finecut.barberbookingmanager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.finecut.barberbookingmanager.adapters.BookingsAdapter
import com.finecut.barberbookingmanager.databinding.ActivityMainBinding
import com.finecut.barberbookingmanager.models.Barbers
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    private var auth = FirebaseAuth.getInstance()

    private lateinit var bookingsAdapter: BookingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        val currentBarberId = auth.currentUser?.uid

        Log.d("Barber ID", currentBarberId!!)



      FirebaseData.DBHelper.getBarberFromDatabase(currentBarberId, object : FirebaseData.DBHelper.BarberCallback{
          override fun onSuccess(barber: Barbers) {

              mainBinding.BookingsTbTitle.text = "${barber.name} Upcoming Bookings"

              Log.d("BarberDATA", barber.toString())
              Log.d("Barber Bookings", barber.bookings.toString())
              val bookingsList: ArrayList<Bookings> = arrayListOf()
              for (booking in barber.bookings){
                  bookingsList.add(booking)
              }

              bookingsList.sortWith { booking1, booking2 ->
                  val dateTime1 = parseDateTime(booking1.date, booking1.timeslot)
                  val dateTime2 = parseDateTime(booking2.date, booking2.timeslot)
                  when {
                      dateTime1 == null -> 1
                      dateTime2 == null -> -1
                      else -> dateTime2.compareTo(dateTime1)
                  }
              }

              bookingsAdapter = BookingsAdapter(this@MainActivity,bookingsList)
              mainBinding.rvBookings.layoutManager = LinearLayoutManager(this@MainActivity)
              mainBinding.rvBookings.adapter = bookingsAdapter

          }

          override fun onFailure(error: DatabaseError) {
              Log.e("Database Error: ", error.toString())
          }
      })

    }

    fun parseDateTime(date: String, timeSlot: String): Date? {
        val dateTime = "$date $timeSlot"
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return try {
            format.parse(dateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

}