package com.finecut.barberbookingmanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.finecut.barberbookingmanager.databinding.ActivityBookingDetailsBinding
import com.finecut.barberbookingmanager.models.Bookings
import com.finecut.barberbookingmanager.models.Users
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

@Suppress("DEPRECATION")
class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var bindingBookingDetails: ActivityBookingDetailsBinding

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

        bindingBookingDetails.tvDetailsUserName.text = "${user.firstName} ${user.surname}"
        bindingBookingDetails.tvPhoneNr.text = user.phoneNumber
        bindingBookingDetails.tvEmail.text = user.email

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
    }
}