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

class VerExamenesActivityViewModel: ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    val examenes = MutableLiveData<List<String>>()
    val showProgressBar = MutableLiveData<Boolean>()

    fun obtenerExamenesObservable(): MutableLiveData<List<String>> {
        return examenes
    }

    fun obtenerShowProgressbarObservable(): MutableLiveData<Boolean> {
        return showProgressBar
    }

    fun obtenerExamenes(curso_id: String) {

        showProgressBar.postValue(true)

        Log.d("obtenerExamenes", "Curso_id: ${curso_id}")
        val call = retroInstance.obtenerExamenes(curso_id)

        call.enqueue(object: Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable){
                showProgressBar.postValue(false)
                examenes.postValue(null)
                logFailure("obtenerExamenes" , t)
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>){
                showProgressBar.postValue(false)
                logResponse("obtenerExamenes", response)

                if(response.isSuccessful){
                    examenes.postValue(response.body())
                } else{
                    examenes.postValue(null)
                }
            }
        })
    }

}