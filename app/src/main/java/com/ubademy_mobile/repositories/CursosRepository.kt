package com.ubademy_mobile.repositories

import android.content.Context
import android.util.Log
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CursosRepository {

    private val baseUrl = Constants.API_CURSOS_URL
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
        return emptyList()
        /*return withContext(Dispatchers.IO) {
            val response = retroInstance.()
            response.body() ?: emptyList()
        }*/
    }

    fun propiosDe(email: String) : List<Curso>{
        return emptyList()
    }

    suspend fun inscriptosDe(email: String): List<Curso> {
        return withContext(Dispatchers.IO) {
            val response = retroInstance.historicos(email)
            response.body() ?: emptyList()
        }
    }

    suspend fun colaboraciones(email: String): List<Curso> {
        return try {
            withContext(Dispatchers.IO) {
                val response = retroInstance.colaboraciones(email)
                response.body() ?: emptyList()
            }
        } catch (e: Throwable) {
            emptyList()
        }
    }

    suspend fun recomendados(email: String): List<Curso> {
        val response = retroInstance.obtenerRecomendados(email)
        return withContext(Dispatchers.IO) {
            response.body() ?: emptyList()
        }
    }

    suspend fun mis_cursos(email: String) : List<Curso>{
        return withContext(Dispatchers.IO) {
            val response = retroInstance.misCursos(email)
            response.body() ?: emptyList()
        }
    }


}