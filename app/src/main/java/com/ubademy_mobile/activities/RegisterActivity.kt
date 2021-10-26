package com.ubademy_mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubademy_mobile.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        BtnFinalizarRegistro.setOnClickListener {
            registrarUsuario()
        }

        BtnVolver.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        finish()
    }
}