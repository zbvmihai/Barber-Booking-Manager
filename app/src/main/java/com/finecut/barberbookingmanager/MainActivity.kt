package com.finecut.barberbookingmanager

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.messaging.FirebaseMessaging
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

    private lateinit var barber: Barbers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        requestNotificationPermission()

        setSupportActionBar(mainBinding.tbBookings)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val currentBarberId = auth.currentUser?.uid

        val bookingsReference =
            firebaseDatabase.getReference("Barbers").child(currentBarberId!!).child("Bookings")

        // This block of code retrieve the authenticated barber from the database and
        // update the views in the Main Activity with the barber data.
        FirebaseData.DBHelper.getBarberFromDatabase(
            currentBarberId,
            object : FirebaseData.DBHelper.BarberCallback {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
                override fun onSuccess(barber: Barbers) {
                    this@MainActivity.barber = barber

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

        // This block of code functionality is to make the app listen to the data in database
        // and update the booking list any time the data is changed. This way the recycler view
        // with the bookings will be constantly updated.
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

                        if (bookingDateTime != null &&
                            (bookingDateTime > currentDateTime ||
                                    (bookingDateTime == currentDateTime && booking.date != SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(currentDate))) &&
                            (booking.bookStatus == 0 || booking.bookStatus == 1)) {
                            bookingsList.add(booking)
                        }
                    }
                }

                // Next lines of code sort the bookings to be displayed based on date and time.
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

            // this function format the date and time with the "dd-MM-yyyy HH:mm" pattern.
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

        // When See Reviews button is clicked, the barber object is passed
        // and the reviews activity will open
        mainBinding.btnSeeReviews.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("barber",barber)
            startActivity(intent)
        }
    }

    //This overridden function will create the options menu in the action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //This overridden function setup the options in options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                logOut()
                true
            }
            R.id.menu_bookings_history -> {
                navigateToBookingHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // this function when executed will log out the user and open the Log In Activity
    private fun logOut() {
            auth.signOut()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()

    }

    // This function navigate to Booking History Class
    private fun navigateToBookingHistory() {
        val intent = Intent(this, BookingHistoryActivity::class.java)
        startActivity(intent)
    }

    // This function set up the notifications channel and notification manager.
    private fun setUpNotification() {
        val channelId = "default_channel"
        val channelName = "Default Channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // This function will request the permission for notifications on devices with Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            } else {
                setUpNotification()
            }
        } else {
            setUpNotification()
        }
    }

    // This function will execute if the permission for notifications is granted.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpNotification()
            } else {
                Toast.makeText(applicationContext, "Permission denied. Notifications won't be enabled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}