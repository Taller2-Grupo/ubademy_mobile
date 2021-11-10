package com.ubademy_mobile.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.VerCursoActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_curso.*

class VerCursoActivity: AppCompatActivity() {

    lateinit var idCurso: String
    lateinit var viewModel: VerCursoActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_curso)

        idCurso = intent.getStringExtra("curso_id").toString()

        initViewModel()
        setup()

        viewModel.getCurso(idCurso)
        viewModel.obtenerInscriptos(idCurso)
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(VerCursoActivityViewModel::class.java)
    }

    private fun observarCurso(){
        viewModel.getCursoObservable().observe(this,{
            Log.d("observar","observando")
        })
    }

    private fun observarCursada(){

        viewModel.getCursadaObservable().observe(this, {

            if(it == null || it.estado == "desinscripto"){
                setBotonDeInscripcion()
            }else{
                setBotonDeDesinscripcion()
            }
        })
    }

    private fun observarInscriptos(){

        val usuario = intent.getStringExtra("usuario").toString()

        viewModel.getInscriptosObservable().observe(this, {

            if(it == null || !it.contains(usuario)){
                setBotonDeInscripcion()
            }else{
                setBotonDeDesinscripcion()
            }
        })
    }

    private fun setup(){

        observarCursada()
        observarCurso()
        observarInscriptos()

        var titulo = intent.getStringExtra("titulo").toString()
        var descripcion = intent.getStringExtra("descripcion").toString()

        LblNombreCursoView.text = titulo
        LblDescripcionCursoView.text = descripcion

        var imagesIntent = Intent(this@VerCursoActivity, VisualizarImagenesActivity::class.java)
        imagesIntent.putExtra("cursoId", idCurso)
        BtnVerImagenes.setOnClickListener {
            startActivity(imagesIntent)
        }

        var inscriptosIntent = Intent(this@VerCursoActivity, VerInscriptosActivity::class.java)
        inscriptosIntent.putExtra("cursoId", idCurso)
        BtnVerAlumnos.setOnClickListener {
            startActivity(inscriptosIntent)
        }

        setBotonDeInscripcion()

    }


    private fun setBotonDeDesinscripcion() {

        val usuario = intent.getStringExtra("usuario").toString()

        Log.d("set Desinscripcion","Usuario a desinscribir: ${usuario}")

        BtnInscribirse.text = "Desinscribirse"
        BtnInscribirse.setBackgroundColor(0xbc0000)
        BtnInscribirse.setOnClickListener {
            viewModel.desinscribirse(usuario)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setBotonDeInscripcion() {

        val usuario = intent.getStringExtra("usuario").toString()
        Log.d("set Inscripcion","Usuario a insribir: ${usuario}")

        BtnInscribirse.text = "Inscribirse"
        BtnInscribirse.setBackgroundColor(R.color.color_primary)
        BtnInscribirse.setOnClickListener {
            viewModel.inscribirse(usuario)
        }
    }

    private fun showAlertaInscripcion(titulo: String, mensaje: String): Boolean {

        var result : Boolean = true
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Proceda") { dialogInterface: DialogInterface, i: Int ->

        }
        builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        return result
    }

}