package com.ubademy_mobile.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.tools.DownloadImageTask
import com.ubademy_mobile.activities.tools.Suscripcion
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.interfaces.CursoService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.view_models.VerCursoActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_curso.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerCursoActivity: AppCompatActivity() {

    lateinit var idCurso: String
    lateinit var viewModel: VerCursoActivityViewModel
    lateinit var userEmail: String
    var inscripcionHabilitada : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_curso)

        idCurso = intent.getStringExtra("curso_id").toString()
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        userEmail = prefs.getString("email", null).toString()

        initViewModel()
        setup()

        viewModel.getCurso(idCurso)
        viewModel.obtenerPerfilDeUsuario(userEmail)
        establecerBotonFav()
    }

    private fun establecerBotonFav() {
        val retroInstance = RetroInstance.getRetroInstance(Constants.API_CURSOS_URL).create(CursoService::class.java)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val call = retroInstance.obtenerEsFavorito(email.toString(), idCurso)

        call.enqueue(object: Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable){
            }

            @SuppressLint("ResourceAsColor")
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>){
                Log.e("body", response.body().toString())
                if (response.body() == true){
                    Log.e("esta faveado", "xd")
                    BtnFavear.text = "Eliminar de favoritos"
                    //BtnFavear.setBackgroundColor(0xbc0000)
                    BtnFavear.setOnClickListener{
                        val usuario = intent.getStringExtra("usuario").toString()
                        viewModel.desfavear(usuario, idCurso)
                        finish()
                        startActivity(intent)
                    }
                }
                else{
                    Log.e("no esta faveado", "xd")
                    BtnFavear.text = "Añadir como favorito"
                    //BtnFavear.setBackgroundColor(R.color.color_primary)
                    BtnFavear.setOnClickListener{
                        val usuario = intent.getStringExtra("usuario").toString()
                        viewModel.favear(usuario, idCurso)
                        finish()
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(VerCursoActivityViewModel::class.java)

        viewModel.usuario.observe(this,{
            if(it?.id != null){
                val curso = viewModel.curso.value
                if (curso?.id != null){
                    Log.d("UsuarioObserver","Seteando boton de inscripcion")
                    setBotonDeInscripcion(it,curso)
                }else{
                    Log.d("UsuarioObserver","Todavia no se obtuvo el curso para " +
                            "setear el boton de inscripcion. Esperando..")
                }
            }else{
                Log.e("UsuarioObserver","Error en el fetch de usuario")
            }
        })

        viewModel.owner.observe(this,{

            if (it?.id != null){
                LblOwner.text = "${it.nombre?.capitalize()} ${it.apellido?.capitalize()}"
                LblOwner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            }
        })
    }

    private fun observarCurso(){
        viewModel.getCursoObservable().observe(this,{

            if (it == null ){
                Log.e("Overservar curso","Curso nulo")
                return@observe
            }

            LblNombreCursoView.setText(it.titulo)
            LblDescripcionCursoView.setText(it.descripcion)
            setLabelSuscripcion(it)
            setBanner(it)
            // Actualizar otros campos ...

            setBotonDeEdicion(it)
            setBotonDeVerAlumnos(it)
            setBotonDeVerExamenes(it)
            viewModel.obtenerPerfilDeOwner(it.id_creador.toString())


            val usuario = viewModel.usuario.value
            if(usuario?.id != null){
                Log.d("CursoObserver","Seteando boton de inscripcion")
                setBotonDeInscripcion(usuario,it)
            }else{
                Log.d("CursoObserver","Todavia no se obtuvo el usuario para " +
                        "setear el boton de inscripcion. Esperando..")
            }
        })
    }

    private fun setBanner(curso: Curso) {
        if(curso.hashtags != "#prueba")
            DownloadImageTask(ImgCurso).execute(curso.hashtags)
    }

    private fun setBotonDeInscripcion(usuario: Usuario, curso: Curso) {

        if(usuario.username == curso.id_creador){
            BtnInscribirse.visibility = View.GONE
            BtnVerImagenes.visibility = View.VISIBLE
            BtnVerAlumnos.visibility = View.VISIBLE
            BtnExamenes.visibility = View.VISIBLE

        }else{

            val suscripcionUser = Suscripcion.valueOf(usuario.tipo_suscripcion.toString().toUpperCase())
            val suscripcionCurso = Suscripcion.valueOf(curso.suscripcion.toString().toUpperCase())

            if (suscripcionUser < suscripcionCurso){

                BtnInscribirse.text = "Comprar suscripción"
                BtnInscribirse.setOnClickListener {
                    startActivity(Intent(this@VerCursoActivity, SuscripcionActivity::class.java))
                }
                BtnInscribirse.isEnabled = true
                inscripcionHabilitada = false
                TxtAvisoComprarSuscripcion.startAnimation(AnimationUtils.loadAnimation(this@VerCursoActivity,R.anim.in_topwards))

            }else{
                viewModel.obtenerInscriptos(idCurso)
                inscripcionHabilitada = true
                BtnInscribirse.isEnabled = true
            }
        }
    }

    private fun setBotonDeVerExamenes(curso: Curso) {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", "").toString()

        var userIsAdmin = curso.colaboradores.firstOrNull { it.username == user }?.username == user

        val examenesIntent = Intent(this@VerCursoActivity, ExamenesActivity::class.java)
        examenesIntent.putExtra("cursoId", idCurso)
        examenesIntent.putExtra("ownerId", curso.id_creador.toString())
        examenesIntent.putExtra("isAdmin", userIsAdmin.toString())

        BtnExamenes.setOnClickListener {
            startActivity(examenesIntent)
        }
    }

    private fun observarCursada(){

        viewModel.getCursadaObservable().observe(this, {

            //if(!inscripcionHabilitada) return@observe

            if(it == null || it.estado == "desinscripto"){

                setBotonDeInscripcion()
                BtnVerAlumnos.visibility = View.GONE
                BtnExamenes.visibility = View.GONE
                BtnVerImagenes.visibility = View.GONE
            }else{
                setBotonDeDesinscripcion()
                BtnVerAlumnos.visibility = View.VISIBLE
                BtnExamenes.visibility = View.VISIBLE
                BtnVerImagenes.visibility = View.VISIBLE
            }
        })
    }


    private fun observarProgressBar() {

        viewModel.getProgressBarObservable().observe(this,{

                if(it) progressBar.visibility= View.VISIBLE
                else progressBar.visibility= View.GONE
        })
    }

    private fun observarInscriptos(){

        val usuario = intent.getStringExtra("usuario").toString()

        viewModel.getInscriptosObservable().observe(this, {

            if(it == null || !it.contains(usuario)){
                setBotonDeInscripcion()
                BtnVerAlumnos.visibility = View.GONE
                BtnExamenes.visibility = View.GONE
                BtnVerImagenes.visibility = View.GONE
            }else{
                setBotonDeDesinscripcion()
                BtnVerAlumnos.visibility = View.VISIBLE
                BtnExamenes.visibility = View.VISIBLE
                BtnVerImagenes.visibility = View.VISIBLE
            }
        })
    }

    private fun setup(){

        observarCursada()
        observarCurso()
        observarInscriptos()
        observarProgressBar()

        var titulo = intent.getStringExtra("titulo").toString()
        var descripcion = intent.getStringExtra("descripcion").toString()

        LblNombreCursoView.text = titulo
        LblDescripcionCursoView.text = descripcion

        var imagesIntent = Intent(this@VerCursoActivity, VisualizarImagenesActivity::class.java)
        imagesIntent.putExtra("cursoId", idCurso)
        BtnVerImagenes.setOnClickListener {
            startActivity(imagesIntent)
        }

        BtnVerAlumnos.setOnClickListener {
            Toast.makeText(this,"No se pudo recuperar el curso",Toast.LENGTH_LONG).show()
        }



        setBotonDeInscripcion()

    }

    private fun setLabelSuscripcion(curso: Curso) {

        when(curso.suscripcion){
            "premium" -> {
                LblSuscripcion.text = curso.suscripcion.toUpperCase()
                ImgSuscripcion.setImageResource(R.drawable.ic_premium)
            }
            "vip" -> {
                LblSuscripcion.text = curso.suscripcion.toUpperCase()
                ImgSuscripcion.setImageResource(R.drawable.ic_vip_colored)
            }
        }
    }

    private fun setBotonDeVerAlumnos(curso: Curso){

        val colaboradores = ArrayList<String>(curso.colaboradores.map { it.username.toString()})

        val inscriptosIntent = Intent(this@VerCursoActivity, VerInscriptosActivity::class.java)
        inscriptosIntent.putExtra("cursoId", idCurso)
        inscriptosIntent.putExtra("ownerId", curso.id_creador.toString())
        inscriptosIntent.putStringArrayListExtra("colaboradores", colaboradores)
        BtnVerAlumnos.setOnClickListener {
            startActivity(inscriptosIntent)
        }
    }

    private fun setBotonDeEdicion(curso: Curso) {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email == null) Log.e("VerCursos","Usuario no logueado")
        else{
            if(email == curso.id_creador) {

                val editIntent = Intent(this@VerCursoActivity, EditarCursoActivity::class.java)
                editIntent.putExtra("cursoId", idCurso)
                editIntent.putExtra("titulo", curso.titulo)
                editIntent.putExtra("descripcion", curso.descripcion)
                editIntent.putExtra("actual_banner", curso.hashtags)

                BtnEditarCurso.visibility = View.VISIBLE
                BtnEditarCurso.setOnClickListener {
                    startActivity(editIntent)
                }
            }
        }
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