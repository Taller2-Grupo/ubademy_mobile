package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.*
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer

class ListadoCursosActivityViewModel: ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"

    var recyclerListData: MutableLiveData<List<Curso>>
    var cursosCopy: MutableLiveData<List<Curso>>

    init{
        recyclerListData = MutableLiveData()
        cursosCopy = MutableLiveData()
    }

    fun getCursosObservable(): MutableLiveData<List<Curso>>{
        return recyclerListData
    }

    fun getCursos(){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)
        val call = retroInstance.obtenerCursos()
        call.enqueue(object: Callback<List<Curso>>{
            override fun onFailure(call: Call<List<Curso>>, t: Throwable){

                logFailure("GetCursos", t)
                recyclerListData.postValue(null)
                cursosCopy.postValue(null)
            }
            override fun onResponse(call: Call<List<Curso>>, response: Response<List<Curso>>){

                logResponse("GetCursos", response)

                if(response.isSuccessful){
                    recyclerListData.postValue(response.body())
                    cursosCopy.postValue(response.body())
                } else{
                    recyclerListData.postValue(null)
                    cursosCopy.postValue(null)
                }
            }
        })
    }

    fun filtrarCursos(searchPreferences : SearchPreferences){

        var filtered = mutableListOf<Curso>()

        cursosCopy.value?.forEach{

            if( searchPreferences.isPatternIn(it.titulo!!) &&
                searchPreferences.isCategory(it.tipo!!) &&
                searchPreferences.isSuscription(it.suscripcion!!)) {

                        filtered.add(it)
            }
        }

        recyclerListData.postValue(filtered)
    }




}