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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.VerCursoActivity
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.ListadoCursosActivityViewModel
import kotlinx.android.synthetic.main.fragment_listado_cursos.*

class ListadoCursosFragment : Fragment(),
    RecyclerViewAdapter.OnItemClickListener,
    AdapterView.OnItemSelectedListener
{

    private var loggedUserEmail: String? = null

    lateinit private var appContext : Context

    private var searchMode = "BASIC"
    lateinit var searchPreferences : SearchPreferences

    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: ListadoCursosActivityViewModel

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

        setup()
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

    private fun setup() {

        searchPreferences = SearchPreferences(appContext)
        initSearchBox()

        BtnFlecha.setOnClickListener {
            toggleSearchMode()
        }

        EditTxTBuscarCurso.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                searchPreferences.patron = s.toString()
                viewModel.filtrarCursos(searchPreferences)
            }
        })

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            appContext,
            R.array.suscripcion,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnCategorias.adapter = adapter
            spnCategorias.onItemSelectedListener = this
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
        viewModel = ViewModelProvider(this).get(ListadoCursosActivityViewModel::class.java)
        viewModel.getCursosObservable().observe(viewLifecycleOwner, {
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


    fun toggleSearchMode(){

        if(searchMode == "BASIC"){

            BtnFlecha.text = "▼"
            SearchBox.visibility = View.VISIBLE
            searchMode = "PRO"

        }else {

            BtnFlecha.text = "▶"
            SearchBox.visibility = View.GONE
            searchMode = "BASIC"
        }

    }

    fun initSearchBox(){
        checkBoxIdioma.isChecked = true
        checkBoxProgramacion.isChecked = true
        checkBoxMultimedia.isChecked = true

        checkBoxIdioma.setOnClickListener{
            if(it is CheckBox)
                if (it.isChecked) searchPreferences.categorias.add(getString(R.string.categoria_idioma))
                else  searchPreferences.categorias.remove(getString(R.string.categoria_idioma))
            viewModel.filtrarCursos(searchPreferences)
        }

        checkBoxProgramacion.setOnClickListener{
            if(it is CheckBox)
                if (it.isChecked) searchPreferences.categorias.add(getString(R.string.categoria_programacion))
                else  searchPreferences.categorias.remove(getString(R.string.categoria_programacion))
            viewModel.filtrarCursos(searchPreferences)
        }

        checkBoxMultimedia.setOnClickListener{
            if(it is CheckBox)
                if (it.isChecked) searchPreferences.categorias.add(getString(R.string.categoria_multimedia))
                else  searchPreferences.categorias.remove(getString(R.string.categoria_multimedia))
            viewModel.filtrarCursos(searchPreferences)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        searchPreferences.suscripcion = parent?.getItemAtPosition(position).toString().lowercase()
        viewModel.filtrarCursos(searchPreferences)

        Log.d("Busqueda filtrada", searchPreferences.toString())

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}
