package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.data.Usuario
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {

    @GET("usuarios")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerUsuarios(): Call<List<Usuario>>

    @GET("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerUsuario(@Path("usuario_id") usuario_id: String): Call<Usuario>

    @POST("usuarios/registrar/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun crearUsuario(@Body params: Usuario): Call<Usuario>

    @PATCH("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun actualizarUsuario(@Path("usuario_id") usuario_id: String, @Body params: Usuario): Call<Usuario>

    @DELETE("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarUsuario(@Path("usuario_id") usuario_id: String): Call<Usuario>
}