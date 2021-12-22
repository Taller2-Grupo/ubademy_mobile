package com.ubademy_mobile.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.ui.text.toUpperCase
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_perfil.titulo
import kotlinx.android.synthetic.main.activity_suscripcion.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuscripcionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suscripcion)

        cargarPantalla()
    }

    private fun cargarPantalla() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email == null) {
            Toast.makeText(this@SuscripcionActivity, "Usuario no logueado.", Toast.LENGTH_LONG).show()
            finish()
        }

        val usuarioService = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL).create(UsuarioService::class.java)
        val call = usuarioService.obtenerUsuario(email!!)

        call.enqueue(object : Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                logFailure("Suscripcion", t)
                Toast.makeText(
                    this@SuscripcionActivity,
                    "Error al obtener el usuario.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }

            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ) {
                logResponse("Suscripcion", response)

                if (response.isSuccessful) {
                    val usuario = response.body()?.data!!

                    suscripcion_actual.text = usuario.tipo_suscripcion!!.toUpperCase()

                    if (usuario.tipo_suscripcion == "gratuita") {
                        btnSuscripcionPremium.visibility = View.VISIBLE
                        btnSuscripcionVip.visibility = View.VISIBLE
                        setBtnSuscripcionPremiumOnClickListener(usuario, usuarioService)
                        setBtnSuscripcionVipOnClickListener(usuario, usuarioService)
                    }

                    if (usuario.tipo_suscripcion == "premium") {
                        btnSuscripcionVip.visibility = View.VISIBLE
                        setBtnSuscripcionVipOnClickListener(usuario, usuarioService)
                    }
                } else {
                    Toast.makeText(this@SuscripcionActivity, "Error al obtener el usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })
    }

    private fun setBtnSuscripcionPremiumOnClickListener(usuario: Usuario, usuarioService: UsuarioService) {
        btnSuscripcionPremium.setOnClickListener {
            val call = usuarioService.suscribirse(usuario.username!!, "premium")

            call.enqueue(object : Callback<UsuarioResponse> {
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    logFailure("Suscripcion", t)
                    Toast.makeText(
                        this@SuscripcionActivity,
                        "Error al intentar suscribir.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: Response<UsuarioResponse>
                ) {
                    logResponse("Suscripcion", response)

                    if (response.isSuccessful) {
                        finish()
                        startActivity(intent)
                    } else {
                        Log.e("Error suscripcion", response.errorBody().toString())
                        Toast.makeText(this@SuscripcionActivity, "Error al intentar suscribir.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            })
        }
    }

    private fun setBtnSuscripcionVipOnClickListener(usuario: Usuario, usuarioService: UsuarioService) {
        btnSuscripcionVip.setOnClickListener {
            val call = usuarioService.suscribirse(usuario.username!!, "vip")

            call.enqueue(object : Callback<UsuarioResponse> {
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    logFailure("Suscripcion", t)
                    Toast.makeText(
                        this@SuscripcionActivity,
                        "Error al intentar suscribir.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: Response<UsuarioResponse>
                ) {
                    logResponse("Suscripcion", response)

                    if (response.isSuccessful) {
                        finish()
                        startActivity(intent)
                    } else {
                        Log.e("Error suscripcion", "Error:" + response.body()?.error!!)
                        Toast.makeText(this@SuscripcionActivity, "Error al intentar suscribir.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            })
        }
    }
}