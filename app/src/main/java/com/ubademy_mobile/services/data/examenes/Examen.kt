package com.ubademy_mobile.services.data.examenes

import com.ubademy_mobile.services.data.examenes.Consigna

data class Examen(
    var nombre: String? = null,
    val id: String? = null,
    val id_curso: String? = null,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null,
    var consignas: List<Consigna>? = null,
    var estado: String? = null)
