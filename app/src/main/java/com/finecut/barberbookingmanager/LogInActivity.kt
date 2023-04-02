package com.finecut.barberbookingmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.finecut.barberbookingmanager.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var bindingLogin : ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingLogin = ActivityLogInBinding.inflate(layoutInflater)
        val view = bindingLogin.root
        setContentView(view)
    }
}