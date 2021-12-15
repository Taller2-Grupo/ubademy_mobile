package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubademy_mobile.repositories.ExamenesRepository
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.examenes.Consigna
import com.ubademy_mobile.services.data.examenes.Examen
import com.ubademy_mobile.services.data.examenes.ExamenResuelto
import com.ubademy_mobile.services.data.examenes.Respuesta
import com.ubademy_mobile.services.interfaces.CursoService
import kotlinx.coroutines.launch

class VerExamenesActivityViewModel: ViewModel() {

    val examen_resuelto = MutableLiveData<ExamenResuelto>()
    var isOwner: Boolean = false
    val baseUrl = "https://ubademy-back.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    val examenes = MutableLiveData<List<Examen>>()
    val nuevo_examen = MutableLiveData<Examen>()
    val showProgressBar = MutableLiveData<Boolean>()

    val respuestas = HashMap<Examen,MutableList<Respuesta>>()

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

        examen_seleccionado = Examen()
        examenes.value?.forEach {

            if (it.id.toString() == id) examen_seleccionado = it
        }
        Log.e("Selecting Exam", "Finding for $id, found ${examen_seleccionado.id}" +
                " by course ${examen_seleccionado.id_curso}")
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

    fun agregarRespuesta(idx_Consigna: Int, respuesta: String) {

        // Nunca debería ser nulo
        val id_consigna = examen_seleccionado.consignas!![idx_Consigna].id

        val nueva_respuesta =
            Respuesta(
            id_consigna = id_consigna,
            resolucion = respuesta
            )

        respuestas.apply {
            if(this.containsKey(examen_seleccionado)) {
                this[examen_seleccionado]!!.forEach {

                    if (it.id_consigna == id_consigna) it.resolucion = nueva_respuesta.resolucion
                    else {
                        this[examen_seleccionado]!!.add(nueva_respuesta)
                    }
                }
            }else{
                    this[examen_seleccionado] = MutableList(1) { nueva_respuesta }
            }

        }
    }

    fun resolverExamen() {

        val examen_resuelto = ExamenResuelto(
            id_examen = examen_seleccionado.id,
            id_curso = examen_seleccionado.id_curso,
            username = iduser,
            respuestas = respuestas[examen_seleccionado] ?: emptyList()
        )

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            this@VerExamenesActivityViewModel.examen_resuelto.postValue(repository.resolverExamen(examen_resuelto))
            showProgressBar.postValue(false)
        }
    }

    fun getExamenResueltoPorUsuario() {

        val id_examen = examen_seleccionado.id

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            examen_resuelto.postValue(
                repository.obtenerExamenDeCursoResueltoPor(id_examen.toString(), idcurso, iduser)
            )

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