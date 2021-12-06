package com.ubademy_mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Examen
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel

class VerExamenActivity : AppCompatActivity() {

    val viewModel = ViewModelProvider(this).get(VerExamenesActivityViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_examen)

        cargarPantalla()
    }

    private fun cargarPantalla() {
        var id = intent.extras?.getString("id")

        if (id == null) {
            Toast.makeText(this@VerExamenActivity, "Examen no especificado.", Toast.LENGTH_LONG)
                .show()
            finish()
        }

        val examen: Examen? = viewModel.obtenerExamen(id.toString())

        //Chequear rol del usuario y mostrar para corregir o completar dependiendo el caso


    }

}