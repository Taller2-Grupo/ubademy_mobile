package com.ubademy_mobile.services

import java.util.*

data class Curso(val id: String?,
                 val id_creador: String?,
                 val titulo: String?,
                 val descripcion: String?,
                 val estado: String?,
                 val fecha_creacion: String?,
                 val fecha_actualizacion: String?,
                 val tipo: String = "idioma",
                 val suscripcion: String = "gratuito",
                 val hashtags: String = "#prueba",
                 val examenes: String = "2",
                 val ubicacion: String = "Argentina")
