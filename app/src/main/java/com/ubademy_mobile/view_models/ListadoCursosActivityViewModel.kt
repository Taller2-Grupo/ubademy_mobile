package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ubademy_mobile.services.*
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListadoCursosActivityViewModel: ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"

    lateinit var recyclerListData: MutableLiveData<List<Curso>>

    init{
        recyclerListData = MutableLiveData()
    }

    fun getCursosObservable(): MutableLiveData<List<Curso>>{
        return recyclerListData
    }

    fun getCursos(){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(RetroService::class.java)
        val call = retroInstance.obtenerCursos()
        call.enqueue(object: Callback<List<Curso>>{
            override fun onFailure(call: Call<List<Curso>>, t: Throwable){

                logFailure("GetCursos", t)
                recyclerListData.postValue(null)
            }
            override fun onResponse(call: Call<List<Curso>>, response: Response<List<Curso>>){

                logResponse("GetCursos", response)

                if(response.isSuccessful){
                    recyclerListData.postValue(response.body())
                } else{
                    recyclerListData.postValue(null)
                }
            }
        })
    }
}