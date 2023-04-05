package com.finecut.barberbookingmanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finecut.barberbookingmanager.R
import com.finecut.barberbookingmanager.databinding.ReviewsCardBinding
import com.finecut.barberbookingmanager.models.Reviews
import com.finecut.barberbookingmanager.models.Users
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ReviewsAdapter(private var context: Context,
private var reviewsList: ArrayList<Reviews>)
    :RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>(){

    inner class ReviewsViewHolder(val adapterBinding: ReviewsCardBinding)
        : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val binding = ReviewsCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {

        FirebaseData.DBHelper.getCurrentUserFromDatabase(reviewsList[holder.adapterPosition].userId, object : FirebaseData.DBHelper.CurrentUserCallback {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(currentUser: Users) {
                holder.adapterBinding.tvClientName.text = "${currentUser.firstName} ${currentUser.surname}"
                holder.adapterBinding.rbClientRating.rating = reviewsList[holder.adapterPosition].rating

                Picasso.get().load(currentUser.image.ifEmpty { context.getString(R.string.userImagePlaceHolder) })
                    .into(holder.adapterBinding.ivClientImage, object :
                        Callback {
                        override fun onSuccess() {
                            holder.adapterBinding.pbClientImage.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            if (e != null) {
                                e.localizedMessage?.let { Log.e("Error Loading Image: ", it) }
                            }
                        }
                    })

                holder.adapterBinding.tvClientDescription.text = reviewsList[holder.adapterPosition].comment
            }

            override fun onFailure(error: DatabaseError) {
                Log.e("Database Error: ",error.toString())
            }
        })
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }
}