package com.ubademy_mobile.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.activity_examenes.*

class ExamenesActivity : AppCompatActivity() {
    private lateinit var viewModel: VerExamenesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examenes)

        val idCurso = intent.getStringExtra("cursoId").toString()
        val idOwner = intent.getStringExtra("ownerId").toString()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", "").toString()

        if(user == "") Log.e("VerExamenesActivity", "Usuario no logueado")

        initViewModel()
        observarProgressBar()

        viewModel.idcurso = idCurso
        viewModel.iduser = user

        BtnBack.setOnClickListener {
            finish()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(VerExamenesActivityViewModel::class.java)

    }

    private fun observarProgressBar() {
        viewModel.obtenerShowProgressbarObservable().observe(this,{
            if(it) progressBar.visibility= View.VISIBLE
            else progressBar.visibility= View.GONE
        })
    }
}