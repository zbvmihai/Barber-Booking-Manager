package com.finecut.barberbookingmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.finecut.barberbookingmanager.databinding.ActivityLogInBinding
import com.finecut.barberbookingmanager.models.Users
import com.finecut.barberbookingmanager.utils.FirebaseData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError

class LogInActivity : AppCompatActivity() {

    private lateinit var bindingLogin : ActivityLogInBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingLogin = ActivityLogInBinding.inflate(layoutInflater)
        val view = bindingLogin.root
        setContentView(view)

        bindingLogin.btnLogIn.setOnClickListener {

            val userEmail = bindingLogin.etLoginEmail.text.toString()
            val userPassword = bindingLogin.etLoginPassword.text.toString()

            if (validateForm(userEmail,userPassword)){
                signIn(userEmail, userPassword)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser !=null){

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signIn(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    FirebaseData.DBHelper.getCurrentUserFromDatabase(userId, object : FirebaseData.DBHelper.CurrentUserCallback {
                        override fun onSuccess(currentUser: Users) {
                            Log.e("USER",currentUser.toString())
                            if (currentUser.client == 0) {
                                Toast.makeText(applicationContext, "User Signed In!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LogInActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "This is not a barber account!", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                            }
                        }

                        override fun onFailure(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                        }
                    })
                } else {
                    Toast.makeText(applicationContext, "User not found!", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            } else {
                Toast.makeText(applicationContext, "Failed to sign in the user. Check email or password!", Toast.LENGTH_SHORT).show()
                Log.e("Error:", task.exception.toString())
            }
        }
    }


    private fun validateForm(email: String, password: String): Boolean {

        return when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(applicationContext, "Please enter an email!", Toast.LENGTH_SHORT)
                    .show()
                false
            }

            TextUtils.isEmpty(password) -> {
                Toast.makeText(applicationContext, "Please enter a password!", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            else -> {
                true
            }
        }
    }
}