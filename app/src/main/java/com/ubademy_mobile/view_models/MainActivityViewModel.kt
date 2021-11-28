package com.ubademy_mobile.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {

    val loggedUser = MutableLiveData<Usuario>()
        get() = field

    val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL)
        .create(UsuarioService::class.java)


    fun getLoggedUser(email : String){

        val call = retroInstance.obtenerUsuario(email)

        call.enqueue(object : Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                logFailure("ListadoCursos", t)
            }

            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ) {

                logResponse("PerfilUsuario", response)

                if (response.isSuccessful) {
                    loggedUser.postValue(response.body()!!.data)
                }
            }
        })
    }
}
