package com.ubademy_mobile.services.data

data class Examen(val nombre: String? = null,
                  val id: String? = null,
                  val id_curso: String? = null,
                  val fecha_creacion: String? = null,
                  val fecha_actualizacion: String? = null,
                  val consignas: List<Consigna>? = null,
                  val estado: String? = null)
