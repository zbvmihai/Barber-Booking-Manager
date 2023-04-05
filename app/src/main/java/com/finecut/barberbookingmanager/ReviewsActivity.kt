package com.finecut.barberbookingmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.finecut.barberbookingmanager.adapters.ReviewsAdapter
import com.finecut.barberbookingmanager.databinding.ActivityReviewsBinding
import com.finecut.barberbookingmanager.models.Barbers
import com.finecut.barberbookingmanager.models.Reviews
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewsBinding

    private lateinit var reviewsAdapter: ReviewsAdapter

    private lateinit var barber: Barbers
    private var reviewsList: ArrayList<Reviews> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.tbReviews)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        barber = intent.getParcelableExtra("barber")!!

        binding.reviewsTbTitle.text = barber.name
        binding.rbReviewsBarberRating.rating = barber.rating

        Picasso.get().load(barber.image.ifEmpty { getString(R.string.userImagePlaceHolder) })
            .into(binding.ivReviewsBarberImage, object :
                Callback {
                override fun onSuccess() {
                    binding.pbReviewsBarberImage.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    if (e != null) {
                        e.localizedMessage?.let { Log.e("Error Loading Image: ", it) }
                    }
                }
            })

        for (review in barber.reviews) {
            reviewsList.add(review)
        }

        reviewsAdapter = ReviewsAdapter(this@ReviewsActivity, reviewsList)
        binding.rvReviews.layoutManager = LinearLayoutManager(this@ReviewsActivity)
        binding.rvReviews.adapter = reviewsAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}