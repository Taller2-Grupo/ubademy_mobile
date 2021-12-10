package com.ubademy_mobile.services.data

data class Examen(
    var nombre: String? = null,
    val id: String? = null,
    val id_curso: String? = null,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null,
    var consignas: List<Consigna>? = null,
    val estado: String? = null)
