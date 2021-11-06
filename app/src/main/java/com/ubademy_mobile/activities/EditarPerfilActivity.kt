package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.android.synthetic.main.activity_editar_perfil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email == null){
            // TODO: El usuario no esta logueado
        }

        val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL).create(UsuarioService::class.java)
        val call = retroInstance.obtenerUsuario(email!!)

        call.enqueue(object: Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable){
                logFailure("EditarPerfilUsuario", t)
                Toast.makeText(this@EditarPerfilActivity, "Error al obtener el usuario.", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>){

                logResponse("PerfilUsuario", response)

                if (response.isSuccessful){
                    cargarDatos(response.body()?.data!!)
                } else{
                    Toast.makeText(this@EditarPerfilActivity, "Error al obtener el usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })

        btnAplicarCambios.setOnClickListener {
            // TODO: Post para actualizar datos
            finish()
        }
    }

    private fun cargarDatos(usuario: Usuario) {
        txtNombre.setText(usuario.nombre)
        txtApellido.setText(usuario.apellido)
    }
}