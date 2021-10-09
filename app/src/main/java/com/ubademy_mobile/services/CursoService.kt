package com.ubademy_mobile.services

import java.util.*

data class Cursos(val data: List<Curso>)
data class Curso(val id: String?, val id_creador: String?, val titulo: String?, val descripcion: String?, val estado: String?, val fecha_creacion: Date?, val fecha_actualizacion: Date?)
data class CursoResponse(val code: Int?, val meta: String?, val data: Curso?)