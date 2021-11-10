package com.ubademy_mobile.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.view_models.CrearCursoActivityViewModel
import androidx.lifecycle.Observer
import com.ubademy_mobile.services.EditarCurso
import kotlinx.android.synthetic.main.activity_editar_curso.*
import kotlinx.android.synthetic.main.activity_ver_curso.BtnEditarCurso

class EditarCursoActivity : AppCompatActivity() {

    lateinit var viewModel: CrearCursoActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_curso)

        initViewModel()

        btnAplicarCambiosCurso.setOnClickListener {
            editarCurso()
            finish()
        }

    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(CrearCursoActivityViewModel::class.java)
    }

    private fun editarCursoObservable(){
        viewModel.getCrearNuevoCursoObservable().observe(this, Observer <Curso?>{
            if(it == null){
                Toast.makeText(this@EditarCursoActivity, "Error al editar el curso", Toast.LENGTH_LONG).show()
            } else{

                Toast.makeText(this@EditarCursoActivity, "Curso editado correctamente", Toast.LENGTH_LONG).show()

                finish()
            }
        })
    }

    private fun editarCurso(){
        val titulo = txtTitulo.text.toString()
        val descripcion = txtDescripcion.text.toString()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val curso = EditarCurso(titulo, descripcion)

        val curso_id = intent.getStringExtra("cursoId")

        if (curso_id == null) {
            return
        }

        viewModel.actualizarCurso(curso_id, curso)
    }

}