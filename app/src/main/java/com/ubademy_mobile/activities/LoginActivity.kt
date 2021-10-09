package com.ubademy_mobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RecyclerViewAdapter

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun BtnLogin_OnClick(view: android.view.View) {
        startActivity(Intent(this@LoginActivity, CrearCursoActivity::class.java))
    }
}