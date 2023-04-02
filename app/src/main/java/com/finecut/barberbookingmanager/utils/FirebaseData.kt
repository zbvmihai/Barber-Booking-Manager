package com.finecut.barberbookingmanager.utils

import com.finecut.barberbookingmanager.models.*
import com.google.firebase.database.*

class FirebaseData {

    object DBHelper {

        private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = firebaseDatabase.getReference("Users")
        val barbersRef: DatabaseReference = firebaseDatabase.getReference("Barbers")
        private val offersRef: DatabaseReference = firebaseDatabase.getReference("Offers")


        interface CurrentUserCallback {
            fun onSuccess(currentUser: Users)
            fun onFailure(error: DatabaseError)
        }

        fun getCurrentUserFromDatabase(userId: String, callback: CurrentUserCallback) {
            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val id = snapshot.child("id").getValue(String::class.java) ?: ""
                    val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                    val surname = snapshot.child("surname").getValue(String::class.java) ?: ""
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val phoneNumber = snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val isClient = snapshot.child("client").getValue(Int::class.java) ?: 1
                    val image = snapshot.child("image").getValue(String::class.java) ?: ""
                    val bookings = snapshot.child("bookings").children.mapNotNull { it.getValue(Bookings::class.java) }
                    val points = snapshot.child("points").getValue(Long::class.java) ?: 0

                    val user = Users(id, firstName, surname, email, phoneNumber, isClient, image, bookings, points)

                    if (id.isNotEmpty()) {
                        callback.onSuccess(user)
                    } else {
                        callback.onFailure(DatabaseError.fromException(Exception("User not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error)
                }
            })
        }

        interface BarberCallback {
            fun onSuccess(barber: Barbers)
            fun onFailure(error: DatabaseError)
        }

        fun getBarberFromDatabase(barberId: String, callback: BarberCallback) {
            barbersRef.child(barberId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val id = snapshot.child("id").getValue(String::class.java) ?: ""
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""
                    val image = snapshot.child("image").getValue(String::class.java) ?: ""
                    val rating = snapshot.child("rating").getValue(Float::class.java) ?: 0F
                    val description = snapshot.child("description").getValue(String::class.java) ?: ""

                    val services = snapshot.child("services").children.mapNotNull { it.getValue(Services::class.java) }
                    val bookings = snapshot.child("Bookings").children.mapNotNull { it.getValue(Bookings::class.java) }
                    val reviews = snapshot.child("reviews").children.mapNotNull { it.getValue(Reviews::class.java)}

                    val barber = Barbers(id, name, image, rating, description, services, bookings, reviews)

                    callback.onSuccess(barber)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error)
                }
            })
        }

    }
}

