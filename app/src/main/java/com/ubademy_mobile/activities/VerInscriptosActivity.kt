package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.InscriptosRecyclerViewAdapter
import com.ubademy_mobile.view_models.VerInscriptosActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_inscriptos.*

class VerInscriptosActivity : AppCompatActivity(), InscriptosRecyclerViewAdapter.OnItemClickListener {


    private lateinit var viewModel: VerInscriptosActivityViewModel
    private lateinit var inscriptosAdapter: InscriptosRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_inscriptos)

        val idCurso = intent.getStringExtra("cursoId").toString()
        val idOwner = intent.getStringExtra("ownerId").toString()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", null)


        if(user != null) initRecyclerView(idOwner,user)
        else Log.e("VerInscriptosActivity", "Usuario no logueado")

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

    private fun initRecyclerView(owner : String, user:String){
        recyclerViewInscriptos.apply {
            layoutManager = LinearLayoutManager(this@VerInscriptosActivity)
            inscriptosAdapter = InscriptosRecyclerViewAdapter(this@VerInscriptosActivity,owner,user)
            adapter = inscriptosAdapter
        }}


    override fun onItemClick(inscripto: String) {

        val intent = Intent(this@VerInscriptosActivity, PerfilActivity::class.java)
        // Le paso el email a PerfilActivity para que muestre el perfil de ese usuario.
        intent.putExtra("email", inscripto)

        startActivity(intent)
    }

}