package com.ubademy_mobile.services.data.examenes

data class Consigna(var enunciado: String? = null,
                    var puntaje: String? = null,
                    val id: String? = null,
                    val fecha_creacion: String? = null,
                    val fecha_actualizacion: String? = null,
                    val id_examen: String? = null,
                    val estado : String? = null,
                    var estadoUser : String? =null )
