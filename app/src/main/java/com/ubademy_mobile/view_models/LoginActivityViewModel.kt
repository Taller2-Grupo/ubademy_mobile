package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Credenciales
import com.ubademy_mobile.services.data.UbademyToken
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivityViewModel {

    val baseUrl = Constants.API_GATEWAY

    var tokenMutableLiveData = MutableLiveData<UbademyToken>()
    var progressBar = MutableLiveData<Boolean>()

    fun getStatusBarObservable(): MutableLiveData<Boolean> {
        return progressBar
    }

    fun getTokenObservable(): MutableLiveData<UbademyToken>{
        return tokenMutableLiveData
    }

    fun loginUsuario(credenciales: Credenciales){

        progressBar.postValue(true)

        val retroInstance = RetroInstance.getRetroInstance("https://ubademy-gateway-7.herokuapp.com/").create(UsuarioService::class.java)
        val call = retroInstance.token(
            username = credenciales.username,
            password = credenciales.password)

        //Log.d("Login Request",call.request().toString())

        call.enqueue(object: Callback<UbademyToken> {
            override fun onFailure(call: Call<UbademyToken>, t: Throwable){
                Log.e("failure", "aaa")
                logFailure("LogearUsuario", t)
                tokenMutableLiveData.postValue(null)
                progressBar.postValue(false)
            }

            override fun onResponse(call: Call<UbademyToken>, response: Response<UbademyToken>){
                Log.e("response", "aaa")
                logResponse("LogearUsuario", response)
                if(response.isSuccessful){
                    Log.e("token response", response.toString())
                    tokenMutableLiveData.postValue(response.body())
                    Log.d("mutable", tokenMutableLiveData.toString())
                } else{
                    Log.e("errorr", response.toString())
                    tokenMutableLiveData.postValue(null)
                }

                progressBar.postValue(false)
            }
        })
    }

    fun swap(token: UbademyToken){

        progressBar.postValue(true)

        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
        val call = retroInstance.swapToken(token.firebase_token)

        Log.d("Loguin Request",call.request().toString())

        call.enqueue(object: Callback<UbademyToken> {
            override fun onFailure(call: Call<UbademyToken>, t: Throwable){
                logFailure("LogearUsuario", t)
                tokenMutableLiveData.postValue(null)
                progressBar.postValue(false)
            }

            override fun onResponse(call: Call<UbademyToken>, response: Response<UbademyToken>){

                logResponse("LogearUsuario", response)
                Log.d("LogearUsuario response", response.body().toString())
                if(response.isSuccessful){
                    tokenMutableLiveData.postValue(response.body())
                } else{
                    tokenMutableLiveData.postValue(null)
                }

                progressBar.postValue(false)
            }
        })
    }

}