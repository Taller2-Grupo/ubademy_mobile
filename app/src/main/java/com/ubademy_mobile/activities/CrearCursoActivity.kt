package com.ubademy_mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.CrearCursoActivityViewModel
import com.ubademy_mobile.services.Curso
import kotlinx.android.synthetic.main.activity_crear_curso.*
import kotlinx.android.synthetic.main.activity_crear_curso.view.*
import kotlinx.android.synthetic.main.recycler_row_list.view.*

class CrearCursoActivity : AppCompatActivity() {

    lateinit var viewModel: CrearCursoActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_curso)

        initViewModel()
        crearCursoObservable()


        BtnCrearCurso.setOnClickListener {
            crearCurso()
        }
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(CrearCursoActivityViewModel::class.java)
    }

    private fun crearCursoObservable(){
        viewModel.getCrearNuevoCursoObservable().observe(this, Observer <Curso?>{
            if(it == null){
                Toast.makeText(this@CrearCursoActivity, "Error al crear el curso", Toast.LENGTH_LONG).show()
            } else{

                Toast.makeText(this@CrearCursoActivity, "Curso creado correctamente (ID: ${it.id})", Toast.LENGTH_LONG).show()


                finish()
            }
        })
    }

    private fun crearCurso(){
        val titulo = TxtTituloNuevoCurso.text.toString()
        val descripcion = TxtDescripcionNuevoCurso.text.toString()

        val curso = Curso(null, "3fa85f64-5717-4562-b3fc-2c963f66afa6", titulo, descripcion, null, null, null)

        viewModel.crearCurso(curso)
    }


}