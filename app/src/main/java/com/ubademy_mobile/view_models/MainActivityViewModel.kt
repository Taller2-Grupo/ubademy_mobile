package com.ubademy_mobile.view_models

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.tools.Themes
import com.ubademy_mobile.repositories.CursosRepository
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse

import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Collectors

class MainActivityViewModel : ViewModel() {

    private var i = 0
    val baseUrl = "https://ubademy-back.herokuapp.com/"

    val loggedUser = MutableLiveData<Usuario?>()

    val cursosByTheme = HashMap<Themes, MutableLiveData<List<Curso>>>()

    val cursos = MutableLiveData<List<Curso>>()
    val cursosCopy = mutableListOf<Curso>()

    val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL)
        .create(UsuarioService::class.java)

    val repository = CursosRepository()

    init {
        Themes.values().forEach {
            cursosByTheme[it] = MutableLiveData()
        }
    }

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

    fun cursos(theme: Themes?): MutableLiveData<List<Curso>> {
        if (theme != null)
            return cursosByTheme[theme]!!
        else
            return cursos
    }

    fun getCursos(email : String, theme : Themes?){

        i += 1
        Log.e("Counter de getCursos",i.toString())

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            if (theme == null){
               cursos.postValue(repository.cursos())
               cursosCopy.addAll(repository.cursos())

            }else{
                var new_cursos : List<Curso>
                when(theme){
                   Themes.CURSOS_POPULARES -> new_cursos = repository.cursos()
                   Themes.CURSOS_FAVORITOS -> new_cursos = repository.favoritosDe(email)
                   Themes.CURSOS_INSCRIPTOS -> new_cursos = repository.inscriptosDe(email)
                   Themes.CURSOS_RECOMENDADOS -> new_cursos = repository.recomendados(email)
                   else -> new_cursos = emptyList()
                }
                cursosByTheme[theme]!!.postValue(new_cursos)
                cursosCopy.addAll(new_cursos)
            }

        }
    }

    fun filtrarCursos(searchPreferences : SearchPreferences){

        var filtered = mutableListOf<Curso>()

        cursosCopy.forEach{

            if( searchPreferences.isPatternIn(it.titulo!!) &&
                searchPreferences.isCategory(it.tipo!!) &&
                searchPreferences.isSuscription(it.suscripcion!!)) {

                filtered.add(it)
            }
        }

        cursos.postValue(filtered)
    }
}
