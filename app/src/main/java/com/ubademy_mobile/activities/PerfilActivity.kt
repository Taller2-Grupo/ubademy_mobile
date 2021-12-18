package com.ubademy_mobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
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

        cargarPantalla()
    }

    override fun onRestart() {
        super.onRestart()

        cargarPantalla()
    }

    private fun cargarPantalla() {
        var email = intent.extras?.getString("email")

        // Error, no se le paso el mail del usuario para mostrar ese perfil.
        if (email == null) {
            Toast.makeText(this@PerfilActivity, "Usuario no especificado.", Toast.LENGTH_LONG).show()
            finish()
        }

        val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL).create(UsuarioService::class.java)
        val call = retroInstance.obtenerUsuario(email!!)

        call.enqueue(object : Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                logFailure("PerfilUsuario", t)
                Toast.makeText(
                    this@PerfilActivity,
                    "Error al obtener el usuario.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }

            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ) {

                logResponse("PerfilUsuario", response)

                if (response.isSuccessful) {
                    cargarDatos(response.body()?.data!!)

                    val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
                    val emailLogueado = prefs.getString("email", null)

                    if (email == emailLogueado) {
                        titulo.text = "Mi perfil"
                        btnEditarPerfil.visibility = VISIBLE
                        btnEditarPerfil.setOnClickListener {
                            startActivity(Intent(this@PerfilActivity, EditarPerfilActivity::class.java))
                        }
                    }
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al obtener el usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })
    }

    fun cargarDatos(usuario: Usuario) {
        titulo.text = "Perfil de ${usuario.nombre} ${usuario.apellido}"
        txtNombre.text = usuario.nombre
        txtApellido.text = usuario.apellido
        txtFechaCreacion.text = usuario.fechaCreacion?.subSequence(0, usuario.fechaCreacion!!.indexOf("T", 0))
    }
}