package com.ubademy_mobile.services.data.examenes

import com.ubademy_mobile.services.data.Cursada

data class ExamenResuelto(
    val id: String? = null,
    val id_examen: String? = null,
    val id_curso: String? = null,
    var username: String? = null,
    val respuestas: List<Respuesta> = emptyList(),
    val estado: String? = null,
    val corrector: String? = null,
    val nota: String? = null,
    val cursada: Cursada? = null,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null,
)
