package com.ubademy_mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel

class VerExamenActivity : AppCompatActivity() {

    lateinit var viewModel : VerExamenesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_examen)

        initViewModel()
        cargarPantalla()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(VerExamenesActivityViewModel::class.java)
    }

    private fun cargarPantalla() {
        var id = intent.extras?.getString("id")

        if (id == null) {
            Toast.makeText(this@VerExamenActivity, "Examen no especificado.", Toast.LENGTH_LONG)
                .show()
            finish()
        }
        Log.e("initExamen, id:", id.toString())
        viewModel.selectExamen(id.toString())

        //Chequear rol del usuario y mostrar para corregir o completar dependiendo el caso


    }

}