package com.ubademy_mobile.services.data.examenes

data class CorreccionRequest(
    val id_examen_resuelto: String? = null,
    val corrector: String? = null,
    val correcciones: List<Correccion>? = emptyList()
    )

