package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.view_models.CrearCursoActivityViewModel
import androidx.lifecycle.Observer
import com.ubademy_mobile.services.EditarCurso
import kotlinx.android.synthetic.main.activity_editar_curso.*
import com.google.android.gms.maps.model.LatLng
import com.ubademy_mobile.Fragments.MapsFragment
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.utils.Constants
import kotlinx.android.synthetic.main.activity_editar_perfil.*
import kotlinx.android.synthetic.main.notificacion.*

class EditarCursoActivity : AppCompatActivity() , OnDataPass{

    private var curso_id: String? = null
    private var _curso: Curso? = null
    private var latitud: Double? = null
    private var longitud: Double? = null

    private  var actual_banner: String? = null
    lateinit var viewModel: CrearCursoActivityViewModel

    override fun onDataPass(data: LatLng) {
        Log.d("LOG", "La latitud es ${data.latitude}")
        Log.d("LOG", "La longitud es ${data.longitude}")
        latitud = data.latitude
        longitud = data.longitude
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_curso)

        initViewModel()
        editarCursoObservable()


        curso_id = intent.getStringExtra("cursoId")
        actual_banner = intent.getStringExtra("actual_banner").toString()

        btnEditFiles.setOnClickListener {
            var imagesIntent = Intent(this@EditarCursoActivity, SubirArchivosActivity::class.java)
            imagesIntent.putExtra("actual_banner",actual_banner)
            imagesIntent.putExtra("CursoId", curso_id)
            startActivity(imagesIntent)
        }

        btnAplicarCambiosCurso.setOnClickListener {
            editarCurso()
            finish()
        }

        viewModel.getCursoData(curso_id)

    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(CrearCursoActivityViewModel::class.java)
        viewModel.loadCursoData.observe(this,{
            if(it?.id != null){
                cargarDatos(it)
            }
        })
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

        val curso = EditarCurso(
            nuevo_titulo = titulo,
            nueva_descripcion = descripcion,
            nueva_latitud = latitud,
            nueva_longitud = longitud
        )

        //val curso_id = intent.getStringExtra("cursoId")

        if (curso_id == null) {
            return
        }

        viewModel.actualizarCurso(curso_id.toString(), curso)
    }

    private fun cargarDatos(curso: Curso) {
        _curso = curso
        txtTitulo.setText(curso.titulo)
        txtDescripcion.setText(curso.descripcion)

        Log.d("CARGAR DATOS", "Latitud: " + curso.latitud.toString())

        if (curso.latitud != null && curso.longitud != null) {
            val bundle = Bundle()
            bundle.putDouble("latitud", curso.latitud)
            bundle.putDouble("longitud", curso.longitud)

            supportFragmentManager.commit {
                add<MapsFragment>(R.id.mapContainerView, args = bundle)
            }
        }
        else {
            supportFragmentManager.commit {
                add<MapsFragment>(R.id.mapContainerView)
            }
        }
    }
}