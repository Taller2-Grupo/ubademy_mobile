package com.ubademy_mobile.view_models

import androidx.lifecycle.MutableLiveData
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivityViewModel {

    val baseUrl = Constants.API_GATEWAY

    var usuarioLiveData = MutableLiveData<Usuario?>()
    var progressBar = MutableLiveData<Boolean>()

    fun getStatusBarObservable(): MutableLiveData<Boolean> {
        return progressBar
    }

    fun getUsuarioObservable(): MutableLiveData<Usuario?>{
        return usuarioLiveData
    }

    fun registrarUsuario(usuario: Usuario){

        progressBar.postValue(true)
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
        val call = retroInstance.crearUsuario(usuario)

        call.enqueue(object: Callback<Usuario> {
            override fun onFailure(call: Call<Usuario>, t: Throwable){
                logFailure("RegistrarUsuario", t)
                usuarioLiveData.postValue(null)

                progressBar.postValue(false)
            }

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>){

                logResponse("RegistrarUsuario", response)

                if(response.isSuccessful){
                    usuarioLiveData.postValue(response.body())
                } else{
                    usuarioLiveData.postValue(null)
                }

                progressBar.postValue(false)
            }
        })
    }

}