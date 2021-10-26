package com.ubademy_mobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        BtnLogin.setOnClickListener {
            login(it)
        }

        BtnRegister.setOnClickListener {
            register(it)
        }
    }

    fun login(view: android.view.View) {
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
    }

    fun register(view: android.view.View) {
        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
    }

}