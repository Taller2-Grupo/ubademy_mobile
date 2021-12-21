package com.ubademy_mobile.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubademy_mobile.repositories.ExamenesRepository
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.examenes.*
import com.ubademy_mobile.services.interfaces.CursoService
import kotlinx.coroutines.launch

class VerExamenesActivityViewModel: ViewModel() {

    val examenes_resueltos = MutableLiveData<List<ExamenResuelto>>()
    val examen_resuelto = MutableLiveData<ExamenResuelto>()
    var isOwner: Boolean = false
    var isAdmin: Boolean = false
    val baseUrl = "https://ubademy-back.herokuapp.com/"
    val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    val examenes = MutableLiveData<List<Examen>>()
    val nuevo_examen = MutableLiveData<Examen>()
    val showProgressBar = MutableLiveData<Boolean>()

    val respuestas = HashMap<Examen,MutableList<Respuesta>>()
    val correcciones = HashMap<String,Pair<Respuesta,Correccion>>()

    val examen_publicado = MutableLiveData<Examen>()

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


    fun obtenerExamenes(curso_id: String){

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            var allExamenes = repository.examenesDeCurso(curso_id)
            if(isOwner || isAdmin) examenes.postValue(allExamenes)
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

            examen_publicado.postValue(repository.publicarExamen(examen_id))
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

    fun seleccionarResuelto(resuelto: ExamenResuelto){

        resuelto.respuestas.forEach{
            correcciones[it.id_consigna.toString()] = Pair(it, Correccion())
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

    fun agregarRespuesta(idx_Consigna: Int, respuesta: String, consigna: String? = null) {

        // Nunca debería ser nulo
        val id_consigna = consigna ?: examen_seleccionado.consignas!![idx_Consigna].id

        val nueva_respuesta =
            Respuesta(
            id_consigna = id_consigna,
            resolucion = respuesta )

        if(respuestas.containsKey(examen_seleccionado)) {
            val existente = respuestas[examen_seleccionado]!!.find { it.id_consigna == id_consigna }
            if (existente == null){
                respuestas[examen_seleccionado]!!.add(nueva_respuesta)
            }else{
                existente.resolucion = nueva_respuesta.resolucion
            }
        }else{
            respuestas[examen_seleccionado] = MutableList(1) { nueva_respuesta }
        }
    }

    fun resolverExamen() {
        examen_seleccionado.consignas?.forEach {
            val respuesta = respuestas[examen_seleccionado]?.firstOrNull{ rta ->
                rta.id_consigna == it.id
            }

            if (respuesta == null) agregarRespuesta(-1, "", it.id)
        }


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

    fun getExamenResueltoPorUsuario(username_resuelto: String? = null) {

        val id_examen = examen_seleccionado.id

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            examen_resuelto.postValue(
                repository.obtenerExamenDeCursoResueltoPor(id_examen.toString(), idcurso, username_resuelto ?: iduser)
            )

            showProgressBar.postValue(false)
        }

    }

    fun getExamenesResueltos() {

        val id_examen = examen_seleccionado.id

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            examenes_resueltos.postValue(
                repository.obtenerExamenesResueltosDeCurso(id_examen.toString(), idcurso)
            )

            showProgressBar.postValue(false)
        }
    }

    fun calificarRespuesta(idxConsigna: Int, esCorrecta: Boolean) {

        // Nunca debería ser nulo
        val id_consigna = examen_seleccionado.consignas!![idxConsigna].id

        correcciones[id_consigna]!!.second.id_respuesta = correcciones[id_consigna]!!.first.id
        correcciones[id_consigna]!!.second.es_correcta = esCorrecta.toString()

        Log.d("calificacion", "Se califica como `$esCorrecta` a la respuesta => ${correcciones[id_consigna]!!.first.resolucion}")
    }

    fun enviarCalificacion(id_resuelto : String){

        val correccionRequest = CorreccionRequest(
                                    id_examen_resuelto = id_resuelto,
                                    corrector = iduser,
                                    correcciones = correcciones.mapValues { it.value.second }.values.toList()
                                )

        showProgressBar.postValue(true)

        // Handlea la llamada en paralelo a las apis
        viewModelScope.launch {

            examen_resuelto.postValue(
                repository.corregirExamen(correccionRequest)
            )

            showProgressBar.postValue(false)
        }
    }

    fun getRespuestaBorrador(idxConsigna: Int): String? {

        val id_consigna = examen_seleccionado.consignas!![idxConsigna].id

        return respuestas[examen_seleccionado]?.firstOrNull {  resp ->
            resp.id_consigna ==id_consigna
        }?.resolucion
    }

    fun getCalificacionBorrador(idxConsigna: Int): String? {

        val id_consigna = examen_seleccionado.consignas!![idxConsigna].id

        return correcciones[id_consigna]?.second?.es_correcta
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


          val correccionRequest =
            CorreccionRequest(
                id_examen_resuelto = null,
                corrector = iduser,
            )
*/

}