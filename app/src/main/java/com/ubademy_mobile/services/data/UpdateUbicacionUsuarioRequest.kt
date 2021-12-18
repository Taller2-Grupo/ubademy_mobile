package com.ubademy_mobile.services.data

data class UpdateUbicacionUsuarioRequest (
    val username: String,
    val latitud: Double?,
    val longitud: Double?
)