package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
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

                val intent = Intent(this@CrearCursoActivity, SubirArchivosActivity::class.java)
                intent.putExtra("CursoId", it.id)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun crearCurso(){
        val titulo = TxtTituloNuevoCurso.text.toString()
        val descripcion = TxtDescripcionNuevoCurso.text.toString()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val curso = Curso(null, prefs.getString("email", null), titulo, descripcion, null, null, null)

        viewModel.crearCurso(curso)
    }


}