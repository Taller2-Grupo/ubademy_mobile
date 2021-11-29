package com.ubademy_mobile.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"

    val loggedUser = MutableLiveData<Usuario>()

    val cursos = MutableLiveData<List<Curso>>()
    val cursosCopy = MutableLiveData<List<Curso>>()

    val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL)
        .create(UsuarioService::class.java)


    fun getLoggedUser(email : String){

        val call = retroInstance.obtenerUsuario(email)

        call.enqueue(object : Callback<UsuarioResponse> {
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                logFailure("ListadoCursos", t)
            }

            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ) {

                logResponse("PerfilUsuario", response)

                if (response.isSuccessful) {
                    loggedUser.postValue(response.body()!!.data)
                }
            }
        })
    }

    fun getCursos(){
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)
        val call = retroInstance.obtenerCursos()
        call.enqueue(object: Callback<List<Curso>>{
            override fun onFailure(call: Call<List<Curso>>, t: Throwable){

                logFailure("GetCursos", t)
                cursos.postValue(null)
                cursosCopy.postValue(null)

            }
            override fun onResponse(call: Call<List<Curso>>, response: Response<List<Curso>>){

                logResponse("GetCursos", response)

                if(response.isSuccessful){
                    cursos.postValue(response.body())
                    cursosCopy.postValue(response.body())
                } else{
                    cursos.postValue(null)
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

        cursos.postValue(filtered)
    }
}
