package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.data.examenes.CorreccionRequest
import com.ubademy_mobile.services.data.examenes.Examen
import com.ubademy_mobile.services.data.examenes.ExamenResuelto
import retrofit2.Response
import retrofit2.http.*

interface ExamenService {

    /* Examenes */

    @POST("examenes/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun crearExamen(@Body examen: Examen): Response<Examen>

    @PATCH("examenes/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun editarExamen(@Body examen: Examen): Response<Examen>

    @GET("examenes/{examen_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerExamen(@Path("examen_id") examen_id: String): Response<Examen>

    @POST("examenes/publicar/{examen_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun publicarExamen(@Path("examen_id") curso_id: String): Response<Examen>

    @POST("examenes/examenes_resueltos/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun crearExamenResuelto(@Body examen_resuelto: ExamenResuelto): Response<ExamenResuelto>

    @GET("examenes/curso/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerExamenesDeCurso(@Path("curso_id") curso_id: String): Response<List<Examen>>

    @GET("examenes/examenes_resueltos/curso/{curso_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerExamenesResueltosDeCurso(@Path("curso_id") curso_id: String): Response<List<Examen>>

    @POST("examenes/examenes_resueltos/corregir")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun corregirExamenResuelto(@Body correccionRequest: CorreccionRequest): Response<ExamenResuelto>
}