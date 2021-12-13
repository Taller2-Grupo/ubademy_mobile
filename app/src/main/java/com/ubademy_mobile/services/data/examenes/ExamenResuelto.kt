package com.ubademy_mobile.services.data

data class ExamenResuelto(
    val id: String? = null,
    val id_examen: String? = null,
    val id_curso: String? = null,
    val username: String? = null,
    val respuestas: List<Respuesta> = emptyList(),
    val estado: String? = null,
    val corrector: String? = null,
    val nota: String? = null,
    val id_cursada: String? = null,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null,
)
