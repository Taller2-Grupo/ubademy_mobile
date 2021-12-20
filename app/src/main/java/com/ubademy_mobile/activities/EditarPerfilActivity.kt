package com.ubademy_mobile.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.gms.maps.model.LatLng
import com.ubademy_mobile.Fragments.MapsFragment
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.UpdateUbicacionUsuarioRequest
import com.ubademy_mobile.services.data.UpdateUsuarioRequest
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

interface OnDataPass {
    fun onDataPass(data: LatLng)
}

class EditarPerfilActivity : AppCompatActivity(), OnDataPass {
    private var _usuario: Usuario? = null
    private val _apiUsuarios = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL).create(UsuarioService::class.java)
    private var latitud: Double? = null
    private var longitud: Double? = null

    override fun onDataPass(data: LatLng) {
        Log.d("LOG", "La latitud es ${data.latitude}")
        Log.d("LOG", "La longitud es ${data.longitude}")
        latitud = data.latitude
        longitud = data.longitude
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        // El usuario no esta logueado
        if (email == null) {
            finish()
        }

        val obtenerUsuarioCall = _apiUsuarios.obtenerUsuario(email!!)

        obtenerUsuarioCall.enqueue(object : Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                logFailure("EditarPerfilUsuario", t)
                Toast.makeText(this@EditarPerfilActivity, "Error al obtener el usuario.", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {

                logResponse("PerfilUsuario", response)

                if (response.isSuccessful) {
                    cargarDatos(response.body()?.data!!)
                } else {
                    Toast.makeText(this@EditarPerfilActivity, "Error al obtener el usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })

        btnAplicarCambios.setOnClickListener {
            val ingresoNombre = !txtNombre.text.isNullOrEmpty()
            val ingresoApellido = !txtApellido.text.isNullOrEmpty()

            if (!ingresoNombre || !ingresoApellido) {
                Toast.makeText(this@EditarPerfilActivity, "No puede dejar campos sin completar.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var updateUsuarioRequest = UpdateUsuarioRequest(_usuario!!.username!!, txtNombre.text.toString(), txtApellido.text.toString())
            val actualizarUsuarioCall = _apiUsuarios.actualizarUsuario(updateUsuarioRequest)

            actualizarUsuarioCall.enqueue(object : Callback<UsuarioResponse> {
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    logFailure("EditarPerfilUsuario", t)
                    Toast.makeText(this@EditarPerfilActivity, "Error al actualizar el usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                    logResponse("PerfilUsuario", response)
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarPerfilActivity, "Usuario actualizado correctamente.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@EditarPerfilActivity, "Error al actualizar el usuario.", Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
            })

            var updateUbicacionRequest = UpdateUbicacionUsuarioRequest(_usuario!!.username!!, latitud, longitud)
            val actualizarUbicacionCall = _apiUsuarios.actualizarUbicacionUsuario(updateUbicacionRequest)

            actualizarUbicacionCall.enqueue(object : Callback<UsuarioResponse> {
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    logFailure("EditarUbicacionUsuario", t)
                    Toast.makeText(this@EditarPerfilActivity, "Error al actualizar la ubicacion.", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                    logResponse("PerfilUsuario Ubicacion", response)
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarPerfilActivity, "Ubicación actualizada.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@EditarPerfilActivity, "Error al actualizar ubicación.", Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
            })

            finish()
        }
    }

    private fun cargarDatos(usuario: Usuario) {
        _usuario = usuario
        txtNombre.setText(usuario.nombre)
        txtApellido.setText(usuario.apellido)
        Log.e("CARGAR DATOS", "Latitud: " + usuario.latitud?.toString())

        if (usuario.latitud != null && usuario.longitud != null) {
            val bundle = Bundle()
            bundle.putDouble("latitud", usuario.latitud)
            bundle.putDouble("longitud", usuario.longitud)

            supportFragmentManager.commit {
                add<MapsFragment>(R.id.mapContainerView, args = bundle)
            }
        }
    }
}