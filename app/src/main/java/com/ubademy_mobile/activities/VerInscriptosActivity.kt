package com.ubademy_mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.InscriptosRecyclerViewAdapter
import com.ubademy_mobile.view_models.VerInscriptosActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_inscriptos.*

class VerInscriptosActivity : AppCompatActivity() {


    private lateinit var viewModel: VerInscriptosActivityViewModel
    private lateinit var inscriptosAdapter: InscriptosRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_inscriptos)

        val idCurso = intent.getStringExtra("cursoId").toString()

        initRecyclerView()

        initViewModel()
        observarInscriptos()
        observarProgressBar()

        viewModel.obtenerInscriptos(idCurso)

    }

    private fun observarProgressBar() {
        viewModel.obtenerShowProgressbarObservable().observe(this,{
            if(it) progressBar.visibility= View.VISIBLE
            else progressBar.visibility= View.GONE
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(VerInscriptosActivityViewModel::class.java)
    }


    private fun observarInscriptos(){
        viewModel.obtenerInscriptosObservable().observe(this,{
            if(it == null || it.isEmpty()) {
                Toast.makeText(this@VerInscriptosActivity, "No hay alumnos inscriptos...", Toast.LENGTH_LONG).show()
            } else{
                inscriptosAdapter.inscriptos = it.toMutableList()
                inscriptosAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initRecyclerView(){
        recyclerViewInscriptos.apply {
            layoutManager = LinearLayoutManager(this@VerInscriptosActivity)
            inscriptosAdapter = InscriptosRecyclerViewAdapter()
            adapter = inscriptosAdapter
        }}


}