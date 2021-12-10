package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubademy_mobile.repositories.ExamenesRepository
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Consigna
import com.ubademy_mobile.services.data.Examen
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerExamenesActivityViewModel: ViewModel() {

    var isOwner: Boolean = false
    val baseUrl = "https://ubademy-back.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    val examenes = MutableLiveData<List<Examen>>()
    val nuevo_examen = MutableLiveData<Examen>()
    val showProgressBar = MutableLiveData<Boolean>()

    var examen_seleccionado = Examen()
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
        return examen_seleccionado
    }


    fun obtenerExamenes(curso_id: String){

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            var allExamenes = repository.examenesDeCurso(curso_id)
            if(isOwner) examenes.postValue(allExamenes)
            else examenes.postValue(filtrarNoPublicados(allExamenes))

            showProgressBar.postValue(false)
        }
    }

    private fun filtrarNoPublicados(allExamenes: List<Examen>): List<Examen> {
        val filtrados = mutableListOf<Examen>()
        allExamenes.forEach{
            if (it.estado == "publicado") filtrados.add(it)
        }
        return filtrados
    }

    fun crearExamen(examen: Examen){

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            nuevo_examen.postValue(repository.crearExamen(examen))

            showProgressBar.postValue(false)
        }
    }

    fun publicarExamen(examen_id : String){

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            repository.publicarExamen(examen_id)
            showProgressBar.postValue(false)
        }
    }

    fun selectExamen(id: String) {
        Log.d("Selecting exam","buscando: ${id}")
        examen_seleccionado = Examen()
        examenes.value?.forEach {
            Log.d("Selecting exam","encontrado: ${it.id.toString()} ")
            if (it.id.toString() == id) examen_seleccionado = it
        }
    }

    fun editarExamenSeleccionado(nombre: String, consignas: MutableList<Consigna>) {

        examen_seleccionado.nombre = nombre
        examen_seleccionado.consignas = consignas

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            nuevo_examen.postValue(repository.editarExamen(examen_seleccionado))
            showProgressBar.postValue(false)
        }
    }
/*

    fun eliminarExamen(examen_id: String) {

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            repository.eliminarExamen(examen_id)
            showProgressBar.postValue(false)
        }
    }
*/

}