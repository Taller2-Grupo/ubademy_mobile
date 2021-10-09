package com.ubademy_mobile.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.CursoResponse
import com.ubademy_mobile.services.Cursos
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.RetroService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListadoCursosActivityViewModel: ViewModel() {

    lateinit var recyclerListData: MutableLiveData<Cursos>

    init{
        recyclerListData = MutableLiveData()
    }

    fun getCursosObservable(): MutableLiveData<Cursos>{
        return recyclerListData
    }

    fun getCursos(){
        val retroInstance = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val call = retroInstance.obtenerCursos()
        call.enqueue(object: Callback<Cursos>{
            override fun onFailure(call: Call<Cursos>, t: Throwable){
                recyclerListData.postValue(null)
            }
            override fun onResponse(call: Call<Cursos>, response: Response<Cursos>){
                if(response.isSuccessful){
                    recyclerListData.postValue(response.body())
                } else{
                    recyclerListData.postValue(null)
                }
            }
        })
    }
}