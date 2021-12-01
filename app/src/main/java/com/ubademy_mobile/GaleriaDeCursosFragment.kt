package com.ubademy_mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.ubademy_mobile.Fragments.ListadoCursosFragment
import com.ubademy_mobile.activities.VerCursoActivity
import com.ubademy_mobile.activities.tools.Themes
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_galeria_de_cursos.*

class GaleriaDeCursosFragment() : Fragment(),
    RecyclerViewAdapter.OnItemClickListener {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var appContext: Context
    private var loggedUserEmail: String? = null
    private var theme: Themes? = null

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loggedUserEmail = it.getString(ARG_LOGGED_USER)

            it.getString(ARG_THEME).toString().apply {
                if (this != "null") theme = Themes.valueOf(this)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        appContext = requireContext()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_galeria_de_cursos, container, false)
    }

    companion object {

        private const val ARG_LOGGED_USER = "loggedUserEmail"
        private const val ARG_THEME = "theme"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GaleriaDeCursosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LOGGED_USER, param1)
                    putString(ARG_THEME, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initViewModel()
        //viewModel.getCursos(loggedUserEmail!!,theme)

        /*when(theme){
            Themes.CURSOS_POPULARES -> TxTTitulo.text = "Cursos populares"
            Themes.CURSOS_FAVORITOS -> TxTTitulo.text = "Mis Favoritos"
            Themes.CURSOS_INSCRIPTOS -> TxTTitulo.text = "Cursos inscriptos"
            Themes.CURSOS_RECOMENDADOS -> TxTTitulo.text = "Recomenadados"
            else -> TxTTitulo.text = "Otros cursos"
        }*/
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(appContext as ViewModelStoreOwner).get(MainActivityViewModel::class.java)
        viewModel.cursos(theme).observe(viewLifecycleOwner, {

            if(it.isEmpty()){
                TxTSinCursos.visibility = View.VISIBLE
            }else{
                TxTSinCursos.visibility = View.INVISIBLE
            }

            recyclerViewAdapter.cursos = it.toMutableList()
            recyclerViewAdapter.notifyDataSetChanged()

        })
    }

    private fun initRecyclerView(){
        RecyclerGaleriaCursos.apply {
            layoutManager = GridLayoutManager(appContext,3)
            addItemDecoration(DividerItemDecoration(appContext, DividerItemDecoration.VERTICAL))
            recyclerViewAdapter = RecyclerViewAdapter(this@GaleriaDeCursosFragment)
            adapter = recyclerViewAdapter
        }
    }

    override fun onItemEditClick(curso: Curso) {
        val intent = Intent(appContext, VerCursoActivity::class.java)
        intent.putExtra("curso_id", curso.id)
        intent.putExtra("descripcion", curso.descripcion)
        intent.putExtra("titulo", curso.titulo)
        intent.putExtra("usuario", loggedUserEmail)
        //usuarios
        startActivity(intent)
    }
}