package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerInscriptosActivityViewModel: ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    val inscriptos = MutableLiveData<List<String>>()
    val showProgressBar = MutableLiveData<Boolean>()

    fun obtenerInscriptosObservable(): MutableLiveData<List<String>> {
        return inscriptos
    }

    fun obtenerShowProgressbarObservable(): MutableLiveData<Boolean> {
        return showProgressBar
    }

    fun obtenerInscriptos(curso_id: String) {

        showProgressBar.postValue(true)

        Log.d("obtenerInscriptos", "Curso_id: ${curso_id}")
        val call = retroInstance.obtenerInscriptos(curso_id)

        call.enqueue(object: Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable){
                showProgressBar.postValue(false)
                inscriptos.postValue(null)
                logFailure("obtenerInscriptos" , t)
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>){
                showProgressBar.postValue(false)
                logResponse("obtenerInscriptos", response)

                if(response.isSuccessful){
                    inscriptos.postValue(response.body())
                } else{
                    inscriptos.postValue(null)
                }
            }
        })
    }

}