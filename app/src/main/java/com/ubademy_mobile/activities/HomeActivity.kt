package com.ubademy_mobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubademy_mobile.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


    }

    fun BtnCrearCurso_OnClick(view: android.view.View) {
        startActivity(Intent(this@HomeActivity, CrearCursoActivity::class.java))
    }

    fun BtnListarCursos_OnClick(view: android.view.View) {
        startActivity(Intent(this@HomeActivity, ListadoCursosActivity::class.java))
    }
}