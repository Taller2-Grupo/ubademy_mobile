package com.ubademy_mobile.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.CursoResponse
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.RetroService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearCursoActivityViewModel: ViewModel() {
    lateinit var crearNuevoCursoLiveData: MutableLiveData<CursoResponse?>
    lateinit var loadCursoData: MutableLiveData<CursoResponse?>
    init{
        crearNuevoCursoLiveData = MutableLiveData()
        loadCursoData = MutableLiveData()
    }

    fun getCrearNuevoCursoObservable(): MutableLiveData<CursoResponse?>{
        return crearNuevoCursoLiveData
    }

    fun getLoadCursoDataObservable(): MutableLiveData<CursoResponse?>{
        return loadCursoData
    }

    fun crearCurso(curso: Curso){
        val retroInstance = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val call = retroInstance.crearCurso(curso)
        call.enqueue(object: Callback<CursoResponse?> {
            override fun onFailure(call: Call<CursoResponse?>, t: Throwable){
                crearNuevoCursoLiveData.postValue(null)
            }

            override fun onResponse(call: Call<CursoResponse?>, response: Response<CursoResponse?>){
                if(response.isSuccessful){
                    crearNuevoCursoLiveData.postValue(response.body())
                } else{
                    crearNuevoCursoLiveData.postValue(null)
                }
            }
        })
    }
    fun actualizarCurso(curso_id: String, curso: Curso){
        val retroInstance = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val call = retroInstance.actualizarCurso(curso_id, curso)
        call.enqueue(object: Callback<CursoResponse?> {
            override fun onFailure(call: Call<CursoResponse?>, t: Throwable){
                crearNuevoCursoLiveData.postValue(null)
            }

            override fun onResponse(call: Call<CursoResponse?>, response: Response<CursoResponse?>){
                if(response.isSuccessful){
                    crearNuevoCursoLiveData.postValue(response.body())
                } else{
                    crearNuevoCursoLiveData.postValue(null)
                }
            }
        })
    }

    fun getCursoData(curso_id: String?){
        val retroInstance = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val call = retroInstance.obtenerCurso(curso_id!!)
        call.enqueue(object: Callback<CursoResponse?> {
            override fun onFailure(call: Call<CursoResponse?>, t: Throwable){
                loadCursoData.postValue(null)
            }

            override fun onResponse(call: Call<CursoResponse?>, response: Response<CursoResponse?>){
                if(response.isSuccessful){
                    loadCursoData.postValue(response.body())
                } else{
                    loadCursoData.postValue(null)
                }
            }
        })
    }
}