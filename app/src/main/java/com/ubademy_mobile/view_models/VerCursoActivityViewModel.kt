package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Cursada
import com.ubademy_mobile.services.data.InscripcionRequest
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerCursoActivityViewModel: ViewModel() {

    var baseUrl = Constants.API_CURSOS_URL
    var curso = MutableLiveData<Curso?>()
    var cursada = MutableLiveData<Cursada?>()
    var inscriptos = MutableLiveData<List<String>>()
    var progressBar = MutableLiveData<Boolean>()

    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    fun getCursoObservable(): MutableLiveData<Curso?> {
        return curso
    }

    fun getCursadaObservable(): MutableLiveData<Cursada?> {
        return cursada
    }

    fun getInscriptosObservable(): MutableLiveData<List<String>> {
        return inscriptos
    }

    fun getProgressBarObservable(): MutableLiveData<Boolean>{
        return progressBar
    }

    fun getCurso(id: String){

        progressBar.postValue(true)

        val call = retroInstance.obtenerCurso(id)

        call.enqueue(object: Callback<Curso> {
            override fun onFailure(call: Call<Curso>, t: Throwable){
                curso.postValue(null)
                logFailure("Obtener curso" , t)
                progressBar.postValue(false)
            }

            override fun onResponse(call: Call<Curso>, response: Response<Curso>){

                logResponse("Obtener curso", response)
                progressBar.postValue(false)
                if(response.isSuccessful){
                    curso.postValue(response.body())
                } else{
                    curso.postValue(null)
                }
            }
        })
    }

    fun obtenerInscriptos(curso_id: String){

        progressBar.postValue(true)

        val call = retroInstance.obtenerInscriptos(curso_id)

        call.enqueue(object: Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable){
                progressBar.postValue(false)
                inscriptos.postValue(null)
                logFailure("obtenerInscriptos" , t)
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>){
                progressBar.postValue(false)
                logResponse("obtenerInscriptos", response)

                if(response.isSuccessful){
                    inscriptos.postValue(response.body())
                } else{
                    inscriptos.postValue(null)
                }
            }
        })
    }

    fun inscribirse(usuario: String) {

        progressBar.postValue(true)

        val call = retroInstance.inscribirUsuario(curso.value?.id.toString(),InscripcionRequest(usuario))

        Log.d("Inscripcion","Se inscribe a ${usuario} de ${curso.value?.id.toString()}")

        call.enqueue(object: Callback<Cursada> {
            override fun onFailure(call: Call<Cursada>, t: Throwable){
                progressBar.postValue(false)
                cursada.postValue(null)
                logFailure("Inscripcion" , t)
            }

            override fun onResponse(call: Call<Cursada>, response: Response<Cursada>){

                progressBar.postValue(false)
                logResponse("Inscripcion", response)

                if(response.isSuccessful){
                    cursada.postValue(response.body())
                } else{
                    cursada.postValue(null)
                }
            }
        })

    }

    fun desinscribirse(usuario: String) {

        progressBar.postValue(true)

        val call = retroInstance.desinscribirUsuario(curso.value?.id.toString(),InscripcionRequest(usuario))

        Log.d("Desinscripcion","Se desinscribe a ${usuario} de ${curso.value?.id.toString()}")

        call.enqueue(object: Callback<Cursada> {
            override fun onFailure(call: Call<Cursada>, t: Throwable){

                progressBar.postValue(false)
                cursada.postValue(null)
                logFailure("Desinscripcion" , t)
            }

            override fun onResponse(call: Call<Cursada>, response: Response<Cursada>){

                progressBar.postValue(false)

                logResponse("Desinscripcion", response)

                if(response.isSuccessful){
                    cursada.postValue(response.body())
                } else{
                    cursada.postValue(null)
                }
            }
        })

    }

}
