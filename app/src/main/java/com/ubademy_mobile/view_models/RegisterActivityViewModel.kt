package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.RetroService
import com.ubademy_mobile.services.UsuarioService
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivityViewModel {

    val baseUrl = "https://ubademy-usuarios.herokuapp.com/"

    var usuarioLiveData = MutableLiveData<Usuario>()

    fun registrarUsuario(usuario: Usuario){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
        val call = retroInstance.crearUsuario(usuario)

        call.enqueue(object: Callback<Usuario> {
            override fun onFailure(call: Call<Usuario>, t: Throwable){
                logFailure("RegistrarUsuario", t)
                usuarioLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>){

                logResponse("RegistrarUsuario", response)

                if(response.isSuccessful){
                    usuarioLiveData.postValue(response.body())
                } else{
                    usuarioLiveData.postValue(null)
                }
            }
        })
    }

}