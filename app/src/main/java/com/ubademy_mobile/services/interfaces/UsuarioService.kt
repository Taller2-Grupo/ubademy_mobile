package com.ubademy_mobile.services.interfaces

import com.ubademy_mobile.services.data.*
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {

    @GET("usuarios")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerUsuarios(): Call<GetUsersResponse>

    @GET("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun obtenerUsuario(@Path("usuario_id") usuario_id: String): Call<UsuarioResponse>

    @POST("usuarios/registrar/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun crearUsuario(@Body params: Usuario): Call<Usuario>

    @PATCH("usuarios/update/")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun actualizarUsuario(@Body params: UpdateUsuarioRequest): Call<UsuarioResponse>

    @DELETE("usuarios/{usuario_id}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun borrarUsuario(@Path("usuario_id") usuario_id: String): Call<Usuario>

    @FormUrlEncoded
    @POST("token")
    @Headers("Accept:application/json")
    fun token(@Field("password") password : String,
              @Field("username") username: String): Call<UbademyToken>

    @GET("token/swap/{firebase_token}")
    @Headers("Accept:application/json", "Content-Type:application/json")
    fun swapToken(@Path("firebase_token") firebase_token: String?): Call<UbademyToken>

}