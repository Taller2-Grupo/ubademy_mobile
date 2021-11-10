package com.ubademy_mobile.services

import java.util.*

data class EditarCurso(val nuevo_titulo: String?  = null,
                       val nueva_descripcion: String?  = null,
                       val nuevo_estado: String?  = null,
                       val nuevo_tipo: String? = "idioma",
                       val nueva_suscripcion: String? = "gratuito",
                       val nuevos_hashtags: String? = "#prueba",
                       val nuevos_examenes: String = "2",
                       val nueva_ubicacion: String = "Argentina")
