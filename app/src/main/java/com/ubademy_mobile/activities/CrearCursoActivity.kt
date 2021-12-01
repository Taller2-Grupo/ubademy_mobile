package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.tools.HashtagManager
import com.ubademy_mobile.view_models.CrearCursoActivityViewModel
import com.ubademy_mobile.services.Curso
import kotlinx.android.synthetic.main.activity_crear_curso.*
import kotlinx.android.synthetic.main.activity_crear_curso.view.*
import kotlinx.android.synthetic.main.recycler_row_list.view.*
import kotlinx.android.synthetic.main.user_item.*

class CrearCursoActivity : AppCompatActivity(), View.OnClickListener  {

    lateinit var viewModel: CrearCursoActivityViewModel
    val hashtagManager = HashtagManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_curso)

        initViewModel()
        crearCursoObservable()


        BtnCrearCurso.setOnClickListener {
            crearCurso()
        }

        // Dropdown list Categorias
        var items = this.resources.getStringArray(R.array.categorias).toMutableList()
        var adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        (DropdownCategorias.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        // Dropdown list Suscripcion
        items = this.resources.getStringArray(R.array.suscripcion).toMutableList()
        items.remove("Todos")
        adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
        (DropdownSuscripcion.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        // Hashtags
        TxtHashtag.editText!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(edited_text: CharSequence, start: Int,
                                       before: Int, count: Int) {

                val strings = edited_text.split("\n")

                if (strings.size <= 1) return

                strings.forEach{ string ->

                    if (hashtagManager.validateHashtag(string)) {
                        val chip = Chip(this@CrearCursoActivity)
                        chip.text = hashtagManager.normalize(string)
                        chip.isCloseIconVisible = true
                        chip.closeIconSize = 25F
                        chip.closeIconStartPadding = 25F
                        chip.setOnCloseIconClickListener(this@CrearCursoActivity)
                        chip.chipIcon = getDrawable(R.drawable.ic_hashtag)
                        chip.chipIconSize = 35F
                        chipGroup.addView(chip)

                        hashtagManager.addHashtag(string)
                    }
                }

                TxtHashtag.editText!!.setText("")
            }
        })
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
        val descripcion = TxtDescripcionNuevoCurso.editText!!.text.toString()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val curso = Curso(
            id_creador = prefs.getString("email", null),
            titulo = titulo,
            descripcion = descripcion,
            tipo = DropdownCategorias.editText!!.text.toString().lowercase(),
            suscripcion = DropdownSuscripcion.editText!!.text.toString().lowercase())

        Log.d("Creating a curso", " titutlo ${curso.titulo}\ndesc ${curso.descripcion} \ncategoria ${curso.tipo}\nsuscripcion ${curso.suscripcion} " )

        viewModel.crearCurso(curso)
    }

    override fun onClick(v: View?) {
        if ( v is Chip ) {
            chipGroup.removeView(v)
            hashtagManager.removeHashtag(v.text as String)
        }
    }

}