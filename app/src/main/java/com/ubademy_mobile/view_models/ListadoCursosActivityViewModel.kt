package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ubademy_mobile.services.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListadoCursosActivityViewModel: ViewModel() {

    lateinit var recyclerListData: MutableLiveData<List<Curso>>

    init{
        recyclerListData = MutableLiveData()
    }

    fun getCursosObservable(): MutableLiveData<List<Curso>>{
        return recyclerListData
    }

    fun getCursos(){
        val retroInstance = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val call = retroInstance.obtenerCursos()
        call.enqueue(object: Callback<List<Curso>>{
            override fun onFailure(call: Call<List<Curso>>, t: Throwable){

                Log.d(
                    "crearCurso onFailure",
                    "Localized message: ${t.localizedMessage!!}\n"+
                            "Cause:             ${t.cause!!}"
                )

                recyclerListData.postValue(null)
            }
            override fun onResponse(call: Call<List<Curso>>, response: Response<List<Curso>>){

                Log.d("crearCurso onResponse",
                    "Message:       ${Gson().toJson(response.message())}) \n" +
                            "Successful: ${response.isSuccessful}\n" +
                            "Body:          ${response.body()}")

                if(response.isSuccessful){
                    recyclerListData.postValue(response.body())
                } else{
                    recyclerListData.postValue(null)
                }
            }
        })
    }
}