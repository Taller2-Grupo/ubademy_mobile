package com.ubademy_mobile.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.ExamenesRecyclerViewAdapter
import com.ubademy_mobile.services.data.examenes.Examen
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_examenes.*

class VerExamenesActivity : AppCompatActivity(), ExamenesRecyclerViewAdapter.OnItemClickListener {

    private lateinit var viewModel: VerExamenesActivityViewModel
    private lateinit var examenesAdapter: ExamenesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_examenes)

        val idCurso = intent.getStringExtra("cursoId").toString()
        val idOwner = intent.getStringExtra("ownerId").toString()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", null)


        if(user != null) initRecyclerView(idOwner,user)
        else Log.e("VerExamenesActivity", "Usuario no logueado")

        initViewModel()
        observarExamenes()
        observarProgressBar()

        viewModel.obtenerExamenes(idCurso)

    }

    private fun observarProgressBar() {
        viewModel.obtenerShowProgressbarObservable().observe(this,{
        if(it) progressBar.visibility= View.VISIBLE
        else progressBar.visibility= View.GONE
        })
        }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(VerExamenesActivityViewModel::class.java)
        }


    @SuppressLint("NotifyDataSetChanged")
    private fun observarExamenes(){
        viewModel.obtenerExamenesObservable().observe(this,{
        if(it == null || it.isEmpty()) {
        Toast.makeText(this@VerExamenesActivity, "No hay consignas disponibles...", Toast.LENGTH_LONG).show()
        } else{
        examenesAdapter.examenes = it.toMutableList()
        examenesAdapter.notifyDataSetChanged()
        }
        })
        }

    private fun initRecyclerView(owner : String, user:String){
        recyclerViewExamenes.apply {
        layoutManager = LinearLayoutManager(this@VerExamenesActivity)
        examenesAdapter = ExamenesRecyclerViewAdapter(this@VerExamenesActivity,owner,user)
        adapter = examenesAdapter
        }}


    override fun onItemClick(examen: Examen) {
        Log.d("VerExamenesActivity", examen.nombre.toString())

        val intent = Intent(this@VerExamenesActivity, VerExamenActivity::class.java)
        // Le paso el email a PerfilActivity para que muestre el perfil de ese usuario.
        intent.putExtra("id", examen.id.toString())

        startActivity(intent)
    }

    override fun setImgStatus(examen: Examen, imageView: ImageView?) {

    }

}
