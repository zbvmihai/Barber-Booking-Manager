package com.finecut.barberbookingmanager.adapters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.finecut.barberbookingmanager.databinding.ActivityBookingDetailsBinding

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var bindingBookingDetails: ActivityBookingDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingBookingDetails = ActivityBookingDetailsBinding.inflate(layoutInflater)
        val view = bindingBookingDetails.root
        setContentView(view)
    }
}