package com.ubademy_mobile.repositories

import android.content.Context
import android.util.Log
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.services.interfaces.UsuarioService
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

        return try{
            withContext(Dispatchers.IO) {
                val response = retroInstance.obtenerCursos()
                response.body() ?: emptyList()
            }
        }catch (e: Throwable) {
            emptyList()
        }
    }

    fun cursosDe(email : String) : List<Curso>{
        return emptyList()
    }

    suspend fun favoritosDe(email : String) : List<Curso>{
        return try{
            withContext(Dispatchers.IO) {
                val response = retroInstance.obtenerfavoritos(email)
                response.body() ?: emptyList()
            }
        }catch (e: Throwable) {
            emptyList()
        }
    }

    fun propiosDe(email: String) : List<Curso>{
        return emptyList()
    }

    suspend fun inscriptosDe(email: String): List<Curso> {
        return try{
            withContext(Dispatchers.IO) {
                val response = retroInstance.historicos(email)
                response.body() ?: emptyList()
            }
        }catch (e: Throwable) {
            emptyList()
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

    suspend fun recomendacionUbicacion(email: String): List<Curso> {
        val baseUrlUsuarios = Constants.API_USUARIOS_URL
        val retroInstanceUsuarios = RetroInstance.getRetroInstance(baseUrlUsuarios).create(UsuarioService::class.java)

        val call = retroInstanceUsuarios.obtenerUsuario2(email)

        Log.e("call", call.body().toString())

        val latitud = call.body()!!.data!!.latitud
        val longitud = call.body()!!.data!!.longitud

        if ( latitud != null && longitud != null ) {
            val responseCursos = retroInstance.obtenerRecomendadosUbicacion(email, latitud, longitud)
            Log.e("ubicacion", responseCursos.body().toString())
            return try{
                withContext(Dispatchers.IO) {
                    responseCursos.body() ?: emptyList()
                }
            }catch (e: Throwable) {
                Log.e("lista vacia ubic", "aaaaa")
                emptyList()
            }
        }
        Log.e("lat y long null", "aaa")
        return emptyList()
    }

    suspend fun getRecomendados(email: String): List<Curso> {
        val recomendadosInteres = recomendados(email)
        val recomendadosUbicacion = recomendacionUbicacion(email)

        return recomendadosInteres + recomendadosUbicacion
    }

    suspend fun recomendados(email: String): List<Curso> {
        val response = retroInstance.obtenerRecomendados(email)
        return try{
            withContext(Dispatchers.IO) {
                response.body() ?: emptyList()
            }
        }catch (e: Throwable) {
            emptyList()
        }
    }

    suspend fun mis_cursos(email: String) : List<Curso>{
        return try {
            withContext(Dispatchers.IO) {
                val response = retroInstance.misCursos(email)
                response.body() ?: emptyList()
            }
        }catch (e: Throwable) {
            emptyList()
        }
    }


}