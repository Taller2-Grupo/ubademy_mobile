package com.ubademy_mobile.services

import com.ubademy_mobile.services.data.Colaborador

data class Curso(val id: String? = null ,
                 val id_creador: String?  = null ,
                 val titulo: String?  = null ,
                 val descripcion: String?  = null ,
                 val estado: String?  = null ,
                 val fecha_creacion: String?  = null ,
                 val fecha_actualizacion: String?  = null ,
                 val tipo: String? = "idioma",
                 val suscripcion: String? = "gratuito",
                 val hashtags: String? = "#prueba",
                 val examenes: String = "2",
                 val ubicacion: String = "Argentina",
                 val colaboradores : List<Colaborador> = emptyList())
