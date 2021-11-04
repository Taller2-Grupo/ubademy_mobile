package com.ubademy_mobile.services.data

data class Credenciales(
    val grant_type: String? = null,
    val username: String = "",
    val password: String = "",
    val scope: String? = null,
    val client_id: String? =null,
    val client_secret: String? = null,
)
