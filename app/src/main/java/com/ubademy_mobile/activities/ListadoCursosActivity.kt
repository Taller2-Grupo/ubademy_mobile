package com.ubademy_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.view_models.ListadoCursosActivityViewModel
import kotlinx.android.synthetic.main.activity_listado_cursos.*

class ListadoCursosActivity: AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {

    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: ListadoCursosActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_cursos)


        initRecyclerView()

        initViewModel()
        viewModel.getCursos()

        BtnNuevoCurso.setOnClickListener {
            startActivity(Intent(this@ListadoCursosActivity, CrearCursoActivity::class.java))
        }
    }

    private fun initRecyclerView(){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListadoCursosActivity)
            val decoration = DividerItemDecoration(this@ListadoCursosActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = RecyclerViewAdapter(this@ListadoCursosActivity)
            adapter = recyclerViewAdapter

        }
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(this).get(ListadoCursosActivityViewModel::class.java)
        viewModel.getCursosObservable().observe(this, Observer<List<Curso>>{
            if(it == null){
                Toast.makeText(this@ListadoCursosActivity, "No hay datos...", Toast.LENGTH_LONG).show()
            } else{
                recyclerViewAdapter.cursos = it.toMutableList()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onItemEditClick(curso: Curso) {
        val intent = Intent(this@ListadoCursosActivity, CrearCursoActivity::class.java)
        intent.putExtra("curso_id", curso.id)
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000){
            viewModel.getCursos()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}