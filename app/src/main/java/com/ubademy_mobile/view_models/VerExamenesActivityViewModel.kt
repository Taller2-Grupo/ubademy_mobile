package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubademy_mobile.repositories.ExamenesRepository
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Examen
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerExamenesActivityViewModel: ViewModel() {

    val baseUrl = "https://ubademy-back.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    val examenes = MutableLiveData<List<Examen>>()
    val examen_seleccionado = MutableLiveData<Examen>()
    val showProgressBar = MutableLiveData<Boolean>()

    val repository = ExamenesRepository()

    lateinit var idcurso : String
    lateinit var iduser: String

    fun obtenerExamenesObservable(): MutableLiveData<List<Examen>> {
        return examenes
    }

    fun obtenerShowProgressbarObservable(): MutableLiveData<Boolean> {
        return showProgressBar
    }

    fun obtenerExamenSeleccionado(): Examen? {
        return examen_seleccionado.value
    }


    fun obtenerExamenes(curso_id: String){

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            examenes.postValue(repository.examenesDeCurso(curso_id))

            showProgressBar.postValue(false)
        }
    }

    fun crearExamen(examen: Examen){

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            examen_seleccionado.postValue(repository.crearExamen(examen))

            showProgressBar.postValue(false)
        }
    }

    fun selectExamen(id: String) {
        Log.d("Selecting exam","buscando: ${id} ")
        examenes.value?.forEach {
            Log.d("Selecting exam","encontrado: ${it.id.toString()} ")
            if (it.id.toString() == id) examen_seleccionado.value = it
        }
    }

}