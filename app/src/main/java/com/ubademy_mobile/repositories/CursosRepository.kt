package com.ubademy_mobile.repositories

import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.CursoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.ubademy_mobile.services.data.InscripcionRequest

class CursosRepository {

    private val baseUrl = "https://ubademy-back.herokuapp.com/"
    private val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(CursoService::class.java)

    suspend fun cursos() : List<Curso>{

        return withContext(Dispatchers.IO) {
            val response = retroInstance.obtenerCursos()
            response.body() ?: emptyList()
        }
    }

    fun cursosDe(email : String) : List<Curso>{
        return emptyList()
    }

    suspend fun favoritosDe(email : String) : List<Curso>{
        return withContext(Dispatchers.IO) {
            val response = retroInstance.obtenerfavoritos(email)
            response.body() ?: emptyList()
        }
    }

    fun propiosDe(email: String) : List<Curso>{
        return emptyList()
    }

    suspend fun historicosDe(email: String): List<Curso> {
        return withContext(Dispatchers.IO) {
            val i = InscripcionRequest(email)
            val response = retroInstance.obtenerhistoricos(email)
            response.body() ?: emptyList()
        }
    }

    suspend fun colaboracionesDe(email: String): List<Curso> {
        return withContext(Dispatchers.IO) {
            val i = InscripcionRequest(email)
            val response = retroInstance.obtenercolaboraciones(email)
            response.body() ?: emptyList()
        }
    }

    fun recomendados(email: String): List<Curso> {
        return emptyList()
    }


}