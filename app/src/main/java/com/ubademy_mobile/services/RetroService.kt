package com.ubademy_mobile.services

import retrofit2.Call
import retrofit2.http.*

interface RetroService {

    @GET("cursos")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerCursos(): Call<List<Curso>>

    @GET("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerCurso(@Path("curso_id") curso_id: String): Call<Curso>

    @POST("cursos/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun crearCurso(@Body params: Curso): Call<Curso>

    @PATCH("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun actualizarCurso(@Path("curso_id") curso_id: String, @Body params: Curso): Call<Curso>

    @DELETE("cursos/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarCurso(@Path("curso_id") curso_id: String): Call<Curso>
}