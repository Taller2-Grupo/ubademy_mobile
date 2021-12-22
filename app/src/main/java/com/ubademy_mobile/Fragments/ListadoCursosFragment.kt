package com.ubademy_mobile.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.MainActivity
import com.ubademy_mobile.activities.VerCursoActivity
import com.ubademy_mobile.activities.tools.Themes
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_suscripcion.*
import kotlinx.android.synthetic.main.fragment_listado_cursos.*

class ListadoCursosFragment : Fragment(),
    RecyclerViewAdapter.OnItemClickListener
{

    private lateinit var theme: Themes
    private var loggedUserEmail: String? = null

    lateinit private var appContext : Context


    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel

    // Obtener parametros
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loggedUserEmail =  it.getString(ARG_LOGGED_USER)
            theme = Themes.valueOf(it.getString(ARG_THEME).toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_listado_cursos, container, false)

        //Obtiene contexto.
        appContext = requireContext()

        // Inflate the layout for this fragment
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initViewModel()
        viewModel.getCursos(loggedUserEmail!!,theme)

        when(theme){
            Themes.CURSOS_INSCRIPTOS -> TxTTitulo.text = "Mis Inscripciones"
            Themes.MIS_CURSOS -> TxTTitulo.text = "Mis Cursos Creados"
            Themes.CURSOS_POPULARES -> TxTTitulo.text = "Cursos populares"
            Themes.CURSOS_FAVORITOS -> TxTTitulo.text = "Mis Favoritos"
            Themes.CURSOS_RECOMENDADOS -> TxTTitulo.text = "Recomenadados"
            Themes.COLABORACIONES -> TxTTitulo.text = "Mis Colaboraciones"
            else -> TxTTitulo.text = "Otros cursos"
        }

        TxTTitulo.setOnClickListener{
            (activity as MainActivity).viewCoursesOf(theme)
        }
    }

    companion object {

        private const val ARG_LOGGED_USER = "loggedUserEmail"
        private const val ARG_THEME = "theme"

        @JvmStatic
        fun newInstance(email: String, theme: String) =
            ListadoCursosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LOGGED_USER, email)
                    putString(ARG_THEME, theme)
                }
            }
    }


    private fun initRecyclerView(){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(appContext,LinearLayoutManager.HORIZONTAL,false)
            val decoration = DividerItemDecoration(appContext, DividerItemDecoration.HORIZONTAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = RecyclerViewAdapter(this@ListadoCursosFragment)
            adapter = recyclerViewAdapter
        }
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(appContext as ViewModelStoreOwner).get(MainActivityViewModel::class.java)
        viewModel.cursos(theme).observe(viewLifecycleOwner, {

            if(it == null || it.isEmpty()){
                TxTSinCursos.visibility = View.VISIBLE
            } else{
                TxTSinCursos.visibility = View.INVISIBLE
                recyclerViewAdapter.cursos = it.toMutableList()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onItemEditClick(curso: Curso) {

        val intent = Intent(appContext, VerCursoActivity::class.java)
        intent.putExtra("curso_id", curso.id)
        intent.putExtra("descripcion", curso.descripcion)
        intent.putExtra("titulo", curso.titulo)
        intent.putExtra("id_creador",curso.id_creador)
        intent.putExtra("suscripcion",curso.suscripcion)
        intent.putExtra("usuario", loggedUserEmail)
        //usuarios
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000){
            viewModel.getCursos(loggedUserEmail!!,theme)
        }
        Log.e("Log de prueba","Pasa por aca")
        super.onActivityResult(requestCode, resultCode, data)
    }
}
