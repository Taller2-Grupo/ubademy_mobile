package com.ubademy_mobile.repositories

import androidx.lifecycle.MutableLiveData
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Examen
import com.ubademy_mobile.services.interfaces.CursoService
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

    /*
    suspend fun eliminarExamen(examenId: String) {
        return withContext(Dispatchers.IO) {
            val response = retroInstance.eliminarExamen(examen)
            response.body()
        }
    }
    */

}