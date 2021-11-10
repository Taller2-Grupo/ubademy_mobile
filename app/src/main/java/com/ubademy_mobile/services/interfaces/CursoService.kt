package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.EditarCurso
import com.ubademy_mobile.services.data.Cursada
import com.ubademy_mobile.services.data.InscripcionRequest
import retrofit2.Call
import retrofit2.http.*

interface CursoService {

    @GET("cursos")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerCursos(): Call<List<Curso>>

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

}