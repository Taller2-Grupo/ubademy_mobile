package com.ubademy_mobile.services

import java.util.*

data class EditarCurso(val nuevo_titulo: String?  = null,
                       val nueva_descripcion: String?  = null,
                       val nuevo_estado: String?  = null,
                       val nuevo_tipo: String? = null,
                       val nueva_suscripcion: String? = null,
                       val nuevos_hashtags: String? = null,
                       val nuevos_examenes: String? = null,
                       val nueva_ubicacion: String? = null)
