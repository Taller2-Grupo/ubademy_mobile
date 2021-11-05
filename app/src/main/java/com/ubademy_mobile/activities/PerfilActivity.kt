package com.ubademy_mobile.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.android.synthetic.main.activity_perfil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        var email = intent.extras?.getString("email")

        if (email == null) {
            // TODO: Error, no se le paso el mail del usuario para mostrar ese perfil.
        }

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val emailLogueado = prefs.getString("email", null)

        if (email == emailLogueado) {
            // TODO: Puede editar.
        }

        val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL).create(UsuarioService::class.java)
        val call = retroInstance.obtenerUsuario(email!!)

        call.enqueue(object: Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable){
                logFailure("PerfilUsuario", t)
                // TODO: Error al obtener datos del usuario.
            }

            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>){

                logResponse("PerfilUsuario", response)

                if (response.isSuccessful){
                    textView.text = "Perfil de ${response.body()?.data?.nombre} ${response.body()?.data?.apellido}"
                } else{
                    // TODO: Error al obtener la info
                }
            }
        })
    }
}