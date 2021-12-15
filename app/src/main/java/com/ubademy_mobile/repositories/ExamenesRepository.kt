package com.ubademy_mobile.repositories

import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.examenes.Examen
import com.ubademy_mobile.services.data.examenes.ExamenResuelto
import com.ubademy_mobile.services.interfaces.ExamenService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExamenesRepository {

    private val baseUrl = "https://ubademy-back.herokuapp.com/"
    private val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(ExamenService::class.java)

    suspend fun examenesDeCurso(id_curso : String) : List<Examen>{

        return withContext(Dispatchers.IO) {
            val response = retroInstance.obtenerExamenesDeCurso(id_curso)
            response.body() ?: emptyList()
        }
    }

    suspend fun crearExamen(nuevo_examen : Examen) : Examen? {

        return withContext(Dispatchers.IO) {
            val response = retroInstance.crearExamen(nuevo_examen)
            response.body()
        }
    }

    suspend fun publicarExamen(id_examen: String){

        return withContext(Dispatchers.IO) {
            val response = retroInstance.publicarExamen(id_examen)
            response.body()
        }
    }

    suspend fun editarExamen(examen: Examen) : Examen? {
        return withContext(Dispatchers.IO) {
            val response = retroInstance.editarExamen(examen)
            response.body()
        }
    }

    suspend fun resolverExamen(examenResuelto: ExamenResuelto): ExamenResuelto? {
        return withContext(Dispatchers.IO) {
            val response = retroInstance.crearExamenResuelto(examenResuelto)
            response.body()
        }
    }

    suspend fun obtenerExamenDeCursoResueltoPor(id_examen: String, idcurso: String, iduser: String): ExamenResuelto? {

        return withContext(Dispatchers.IO) {
            val response = retroInstance.obtenerExamenResueltoDeCursoPorUsuario(idcurso,iduser)
            var result : ExamenResuelto? = null
            response.body()?.forEach{
                if(it.id_examen == id_examen) {
                    result = it
                    return@forEach
                }
            }
            result
        }
    }

    /*
    suspend fun eliminarExamen(examenId: String) {
        return withContext(Dispatchers.IO) {
            val response = retroInstance.eliminarExamen(examen)
            response.body()
        }
    }
    */

}