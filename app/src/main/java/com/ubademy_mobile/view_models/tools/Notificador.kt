package com.ubademy_mobile.view_models.tools

import android.util.Log
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Device
import com.ubademy_mobile.services.data.Notificacion
import com.ubademy_mobile.services.interfaces.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun notificar(username: String, titulo: String, body: String){

    val baseUrl = "https://ubademy-usuarios.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
    val call = retroInstance.notificar(Notificacion(username = username, title = titulo, body = body))

    call.enqueue(object: Callback<Notificacion> {
        override fun onFailure(call: Call<Notificacion>, t: Throwable){
            Log.d("onFailure", t.localizedMessage)
        }

        override fun onResponse(call: Call<Notificacion>, response: Response<Notificacion>){

        }
    })

}