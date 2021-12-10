package com.ubademy_mobile.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.VerExamenActivity
import com.ubademy_mobile.services.ExamenesRecyclerViewAdapter
import com.ubademy_mobile.services.data.Examen
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_examenes.*
import kotlinx.android.synthetic.main.activity_ver_examenes.recyclerViewExamenes
import kotlinx.android.synthetic.main.fragment_ver_examenes.*
import android.content.DialogInterface




class VerExamenesFragment : Fragment(), ExamenesRecyclerViewAdapter.OnItemClickListener {

    private lateinit var appContext: Context
    val viewModel: VerExamenesActivityViewModel by activityViewModels()
    private lateinit var examenesAdapter: ExamenesRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_ver_examenes, container, false)

        // Set context
        appContext = requireContext()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        // TODO
//        val idCurso = appContext.intent.getStringExtra("cursoId").toString()
//        val idOwner = appContext.intent.getStringExtra("ownerId").toString()
        val idCurso = "a"
        val idOwner = "a"

        val prefs = appContext.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", null)


        if(user != null) initRecyclerView(idOwner,user)
        else Log.e("VerExamenesActivity", "Usuario no logueado")

        initViewModel()
        observarExamenes()

        viewModel.obtenerExamenes(viewModel.idcurso)
        viewModel.selectExamen("")

        if(viewModel.isOwner) {
            FabNuevoExamen.visibility = View.VISIBLE
            FabNuevoExamen.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.ActionCreateExamen)
            }
        }
    }

    private fun initViewModel() {
        //viewModel = ViewModelProvider(requireActivity() as ViewModelStoreOwner).get(VerExamenesActivityViewModel::class.java)
    }

    private fun observarExamenes(){
        viewModel.obtenerExamenesObservable().observe(viewLifecycleOwner,{
            if(it == null || it.isEmpty()) {
                Toast.makeText(appContext, "No hay consignas disponibles...", Toast.LENGTH_LONG).show()
            } else{
                examenesAdapter.examenes = it.toMutableList()
                examenesAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initRecyclerView(owner : String, user:String){
        recyclerViewExamenes.apply {
            layoutManager = LinearLayoutManager(appContext)
            examenesAdapter = ExamenesRecyclerViewAdapter(this@VerExamenesFragment,owner,user)
            adapter = examenesAdapter
        }}


    override fun onItemClick(examen: Examen) {

        Log.d("VerExamenesActivity", examen.nombre.toString())
        viewModel.selectExamen(examen.id.toString())
        Navigation.findNavController(requireView()).navigate(R.id.SelectExamen)
    }

    override fun setImgStatus(examen: Examen, imageView: ImageView?) {

        if (!viewModel.isOwner) return

        imageView!!.visibility = View.VISIBLE

        if (examen.estado == "creado"){
            imageView.setImageResource(R.drawable.ic_private)
            imageView.setOnClickListener {
                publicarExamen(examen.id.toString())
            }
        }
    }

    fun publicarExamen(id_examen : String){
        val alert: AlertDialog.Builder = AlertDialog.Builder(appContext)
        alert.setTitle("Publicar examen")
        alert.setMessage("Desea publicar el examen? No se podrá volver a editar")
        alert.setPositiveButton("Sí") { dialog, which ->

            viewModel.publicarExamen(examen_id = id_examen)
            dialog.dismiss()
        }

        alert.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        alert.show()
    }
}

