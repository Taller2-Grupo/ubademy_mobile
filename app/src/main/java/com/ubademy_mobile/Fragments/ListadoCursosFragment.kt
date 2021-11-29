package com.ubademy_mobile.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.VerCursoActivity
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_listado_cursos.*

class ListadoCursosFragment : Fragment(),
    RecyclerViewAdapter.OnItemClickListener
{

    private var loggedUserEmail: String? = null

    lateinit private var appContext : Context


    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel

    // Obtener parametros
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loggedUserEmail =  it.getString(ARG_LOGGED_USER)
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
        viewModel.getCursos()

    }

    companion object {

        private const val ARG_LOGGED_USER = "loggedUserEmail"

        @JvmStatic
        fun newInstance(param1: String) =
            ListadoCursosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LOGGED_USER, param1)
                }
            }
    }


    private fun initRecyclerView(){
        recyclerView.apply {

            layoutManager = LinearLayoutManager(appContext)
            val decoration = DividerItemDecoration(appContext, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = RecyclerViewAdapter(this@ListadoCursosFragment)
            adapter = recyclerViewAdapter
        }
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(appContext as ViewModelStoreOwner).get(MainActivityViewModel::class.java)
        viewModel.cursos.observe(viewLifecycleOwner, {

            if(it == null){
                Toast.makeText(appContext, "No hay datos...", Toast.LENGTH_LONG).show()
            } else{
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
        intent.putExtra("usuario", loggedUserEmail)
        //usuarios
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000){
            viewModel.getCursos()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
