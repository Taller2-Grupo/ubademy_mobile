package com.ubademy_mobile.repositories

import android.content.Context
import android.util.Log
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    fun favoritosDe(email : String) : List<Curso>{
        return emptyList()
    }

    fun propiosDe(email: String) : List<Curso>{
        return emptyList()
    }

    fun inscriptosDe(email: String): List<Curso> {
        return emptyList()
    }

    fun recomendados(email: String): List<Curso> {
        return emptyList()
    }


}