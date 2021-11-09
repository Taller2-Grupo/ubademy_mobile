package com.ubademy_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.ubademy_mobile.R
import kotlinx.android.synthetic.main.activity_ver_curso.*

class ViewCursoActivity: AppCompatActivity() {

    lateinit var idCurso: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_curso)

        setup()
    }

    private fun setup(){
        idCurso = intent.getStringExtra("curso_id").toString()
        var titulo = intent.getStringExtra("titulo").toString()
        var descripcion = intent.getStringExtra("descripcion").toString()

        LblDescripcionCursoView.text = descripcion
        LblNombreCursoView.text = titulo

        var intent = Intent(this@ViewCursoActivity, VisualizarImagenesActivity::class.java)
        intent.putExtra("cursoId", idCurso)
        BtnVerImagenes.setOnClickListener {
            startActivity(intent)
        }
    }
}