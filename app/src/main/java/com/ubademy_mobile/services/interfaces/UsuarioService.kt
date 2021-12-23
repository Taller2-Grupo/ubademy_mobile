package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.data.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UsuarioService {

    @GET("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    suspend fun obtenerUsuario2(@Path("usuario_id") usuario_id: String): Response<UsuarioResponse>

    @GET("usuarios?estado=activo")
    @Headers("Accept:application/json", "Content-Type:application/json", "Authorization: Bearer ")
    fun obtenerUsuarios(@Query("nombre") nombre: String? = null, @Query("apellido") apellido: String? = null): Call<GetUsersResponse>

    @GET("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerUsuario(@Path("usuario_id") usuario_id: String): Call<UsuarioResponse>

    @POST("usuarios/registrar/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun crearUsuario(@Body params: Usuario): Call<Usuario>

    @PATCH("usuarios/update/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun actualizarUsuario(@Body params: UpdateUsuarioRequest): Call<UsuarioResponse>

    @PATCH("usuarios/ubicacion/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun actualizarUbicacionUsuario(@Body params: UpdateUbicacionUsuarioRequest): Call<UsuarioResponse>

    @DELETE("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarUsuario(@Path("usuario_id") usuario_id: String): Call<Usuario>

    @FormUrlEncoded
    @POST("token")
    @Headers("Accept:application/json")
    fun token(
        @Field("password") password: String,
        @Field("username") username: String
    ): Call<UbademyToken>

    @GET("token/swap/{firebase_token}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun swapToken(@Path("firebase_token") firebase_token: String?): Call<UbademyToken>

    /* devices */
    @POST("usuarios/devices")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun registrarDevice(@Body device: Device): Call<Device>

    @DELETE("usuarios/devices/{device}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarDevice(@Path("device") device: String): Call<Device>

    @POST("usuarios/notify")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun notificar(@Body notificacion: Notificacion): Call<Notificacion>

    @POST("usuarios/{username}/suscripcion/{suscripcion}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun suscribirse(@Path("username") username: String, @Path("suscripcion") suscripcion: String): Call<UsuarioResponse>
}