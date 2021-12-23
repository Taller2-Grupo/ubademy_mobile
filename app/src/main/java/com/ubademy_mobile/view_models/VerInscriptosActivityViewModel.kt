package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Colaborador
import com.ubademy_mobile.services.data.ColaboradorRequest
import com.ubademy_mobile.services.data.GetUsersResponse
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerInscriptosActivityViewModel: ViewModel() {

    val cursoRetroInstance = RetroInstance.getRetroInstance(Constants.API_CURSOS_URL).create(CursoService::class.java)
    val usuarioRetroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL).create(UsuarioService::class.java)

    val inscriptos = MutableLiveData<List<String>>()
    val showProgressBar = MutableLiveData<Boolean>()
    val usuarios = MutableLiveData<List<Usuario>>()
    val colaborador = MutableLiveData<Colaborador?>()

    fun obtenerInscriptosObservable(): MutableLiveData<List<String>> {
        return inscriptos
    }

    fun obtenerShowProgressbarObservable(): MutableLiveData<Boolean> {
        return showProgressBar
    }

    fun obtenerUsuarios(){

        showProgressBar.postValue(true)
        val call = usuarioRetroInstance.obtenerUsuarios()

        call.enqueue(object: Callback<GetUsersResponse> {
            override fun onFailure(call: Call<GetUsersResponse>, t: Throwable){
                showProgressBar.postValue(false)
                usuarios.postValue(emptyList())
                logFailure("obtenerInscriptos" , t)
            }

            override fun onResponse(call: Call<GetUsersResponse>, response: Response<GetUsersResponse>){
                showProgressBar.postValue(false)
                logResponse("obtenerInscriptos", response)

                if(response.isSuccessful){
                    usuarios.postValue(response.body()?.data)
                } else{
                    usuarios.postValue(emptyList())
                }
            }
        })
    }

    fun obtenerInscriptos(curso_id: String) {

        showProgressBar.postValue(true)

        Log.d("obtenerInscriptos", "Curso_id: ${curso_id}")
        val call = cursoRetroInstance.obtenerInscriptos(curso_id)

        call.enqueue(object: Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>, t: Throwable){
                showProgressBar.postValue(false)
                inscriptos.postValue(emptyList())
                logFailure("obtenerInscriptos" , t)
            }

            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>){
                showProgressBar.postValue(false)
                logResponse("obtenerInscriptos", response)

                if(response.isSuccessful){
                    inscriptos.postValue(response.body())
                } else{
                    inscriptos.postValue(emptyList())
                }
            }
        })
    }

    fun eliminarColaborador(usuario: Usuario, id_curso : String ) {

        showProgressBar.postValue(true)

        val request = ColaboradorRequest(
            id_curso = id_curso,
            username = usuario.username
        )

        val call = cursoRetroInstance.eliminarColaborador(request)

        call.enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable){
                showProgressBar.postValue(false)
                logFailure("obtenerInscriptos" , t)
            }

            override fun onResponse(call: Call<String>, response: Response<String>){
                showProgressBar.postValue(false)
                logResponse("obtenerInscriptos", response)
                if(response.isSuccessful){
                    colaborador.postValue(null)
                }
            }
        })
    }

    fun agregarColaborador(usuario: Usuario, id_curso: String) {

        showProgressBar.postValue(true)

        val request = ColaboradorRequest(
            id_curso = id_curso,
            username = usuario.username
        )

        val call = cursoRetroInstance.agregarColaborador(request)

        call.enqueue(object: Callback<Colaborador> {
            override fun onFailure(call: Call<Colaborador>, t: Throwable){
                showProgressBar.postValue(false)
                logFailure("obtenerInscriptos" , t)
            }

            override fun onResponse(call: Call<Colaborador>, response: Response<Colaborador>){
                showProgressBar.postValue(false)
                logResponse("obtenerInscriptos", response)
                Log.e("AgregarColaborador", "${response.code()}")
                if(response.isSuccessful){
                    colaborador.postValue(response.body())
                }
            }
        })

    }

}