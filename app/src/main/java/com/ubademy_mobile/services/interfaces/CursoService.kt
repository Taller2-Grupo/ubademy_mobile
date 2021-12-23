package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.EditarCurso
import com.ubademy_mobile.services.data.*
import com.ubademy_mobile.services.data.examenes.Examen
import okhttp3.internal.http.hasBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CursoService {

    @GET("cursos?estado=activo")
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

    @GET("cursos/historicos/{username}/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun historicos(@Path("username") username: String): Response<List<Curso>>

    @GET("cursos/colaboraciones/{username}/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun colaboraciones(@Path("username") username: String): Response<List<Curso>>

    @GET("recomendaciones/intereses/{username}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerRecomendados(@Path("username") username: String): Response<List<Curso>>

    @GET("recomendaciones/ubicacion/{username}/{latitud}/{longitud}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerRecomendadosUbicacion(@Path("username") username: String, @Path("latitud") latitud: Double, @Path("longitud") longitud: Double): Response<List<Curso>>

    @GET("{creador}/cursos")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun misCursos(@Path("creador") creador: String): Response<List<Curso>>

    @DELETE("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarCurso(@Path("curso_id") curso_id: String): Call<Curso>


    @POST("cursos/colaborador")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun agregarColaborador(@Body colaboradorRequest: ColaboradorRequest): Call<Colaborador>

    @DELETE("cursos/colaborador/delete")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun eliminarColaborador(@Body colaboradorRequest: ColaboradorRequest): Call<String>

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

    @GET("examenes/curso/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerExamenes(@Path("curso_id") curso_id: String): Call<List<Examen>>

    @GET("cursos/favoritos/{username}/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerfavoritos(@Path("username") username: String): Response<List<Curso>>

    @GET("cursos/favoritos/{username}/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerEsFavorito(@Path("username") username: String, @Path("curso_id") curso_id: String): Call<Boolean>

    @POST("cursos/favoritos/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun favear(@Body fav: FavearRequest): Call<FavearRequest>

    @DELETE("cursos/favoritos/{username}/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun desfavear(@Path("username") username: String, @Path("curso_id") curso_id: String): Call<Boolean>

}