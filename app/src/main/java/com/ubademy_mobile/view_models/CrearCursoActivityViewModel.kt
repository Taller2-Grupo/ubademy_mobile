package com.ubademy_mobile.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.EditarCurso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CrearCursoActivityViewModel: ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"

    lateinit var crearNuevoCursoLiveData: MutableLiveData<Curso?>
    lateinit var loadCursoData: MutableLiveData<Curso?>


    init{
        crearNuevoCursoLiveData = MutableLiveData()
        loadCursoData = MutableLiveData()
    }

    fun getCrearNuevoCursoObservable(): MutableLiveData<Curso?>{
        return crearNuevoCursoLiveData
    }

    fun getLoadCursoDataObservable(): MutableLiveData<Curso?>{
        return loadCursoData
    }

    fun crearCurso(curso: Curso){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)
        val call = retroInstance.crearCurso(curso)

        call.enqueue(object: Callback<Curso> {
            override fun onFailure(call: Call<Curso>, t: Throwable){
                crearNuevoCursoLiveData.postValue(null)
                logFailure("CrearCurso" , t)
            }

            override fun onResponse(call: Call<Curso>, response: Response<Curso>){

                logResponse("CrearCurso", response)

                if(response.isSuccessful){
                    crearNuevoCursoLiveData.postValue(response.body())
                } else{
                    crearNuevoCursoLiveData.postValue(null)
                }
            }
        })
    }

    fun actualizarCurso(curso_id: String, curso: EditarCurso){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)
        val call = retroInstance.actualizarCurso(curso_id, curso)
        call.enqueue(object: Callback<Curso> {
            override fun onFailure(call: Call<Curso>, t: Throwable){
                crearNuevoCursoLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Curso>, response: Response<Curso>){
                if(response.isSuccessful){
                    crearNuevoCursoLiveData.postValue(response.body())
                } else{
                    crearNuevoCursoLiveData.postValue(null)
                }
            }
        })
    }

    fun getCursoData(curso_id: String?){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)
        val call = retroInstance.obtenerCurso(curso_id!!)
        call.enqueue(object: Callback<Curso?> {
            override fun onFailure(call: Call<Curso?>, t: Throwable){
                loadCursoData.postValue(null)
            }

            override fun onResponse(call: Call<Curso?>, response: Response<Curso?>){
                if(response.isSuccessful){
                    loadCursoData.postValue(response.body())
                } else{
                    loadCursoData.postValue(null)
                }
            }
        })
    }
}