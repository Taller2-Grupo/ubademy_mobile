package com.ubademy_mobile.repositories

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



}