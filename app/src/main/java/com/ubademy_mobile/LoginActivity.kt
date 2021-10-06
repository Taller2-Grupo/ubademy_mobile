package com.ubademy_mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun BtnLogin_OnClick(view: android.view.View) {
        setContentView(R.layout.activity_home)
    }
}