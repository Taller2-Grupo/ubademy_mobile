package com.ubademy_mobile.services.data

data class Usuario(
    val id: String? = null,
    val username: String?,
    val nombre: String?,
    val apellido: String?,
    val password: String?,
    val esAdmin: String? = null,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)