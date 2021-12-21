package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.EditarCurso
import com.ubademy_mobile.services.data.Cursada
import com.ubademy_mobile.services.data.FavearRequest
import com.ubademy_mobile.services.data.Favorito
import com.ubademy_mobile.services.data.InscripcionRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CursoService {

    @GET("cursos")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerCursos(): Response<List<Curso>>

    @GET("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerCurso(@Path("curso_id") curso_id: String): Call<Curso>

    @POST("cursos/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun crearCurso(@Body params: Curso): Call<Curso>

    @PUT("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun actualizarCurso(@Path("curso_id") curso_id: String, @Body params: EditarCurso): Call<Curso>

    @DELETE("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarCurso(@Path("curso_id") curso_id: String): Call<Curso>

    /* inscripciones */

    @POST("cursos/{curso_id}/inscribirse")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun inscribirUsuario(@Path("curso_id") curso_id: String, @Body params: InscripcionRequest): Call<Cursada>

    @PUT("cursos/{curso_id}/desinscribirse")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun desinscribirUsuario(@Path("curso_id") curso_id: String, @Body params: InscripcionRequest): Call<Cursada>

    @GET("cursos/{curso_id}/alumnos")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerInscriptos(@Path("curso_id") curso_id: String): Call<List<String>>

    @GET("cursos/favoritos/{username}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerfavoritos(@Path("username") username: String): Response<List<Curso>>

    @GET("cursos/historicos/{username}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerhistoricos(@Path("username") username: String): Response<List<Curso>>

    @GET("cursos/colaboraciones/{username}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenercolaboraciones(@Path("username") username: String): Response<List<Curso>>

    @POST("cursos/favoritos")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun favear(@Body fav: FavearRequest): Call<FavearRequest>

}