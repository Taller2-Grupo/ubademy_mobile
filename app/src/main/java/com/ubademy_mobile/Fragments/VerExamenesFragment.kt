package com.ubademy_mobile.Fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.ExamenesRecyclerViewAdapter
import com.ubademy_mobile.services.data.examenes.Examen
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.fragment_ver_examenes.*
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.graphics.toColorInt


class VerExamenesFragment : Fragment(), ExamenesRecyclerViewAdapter.OnItemClickListener {

    private var avisoSalidaAnim: Animation? = null
    private var avisoEntradaAnim: Animation? = null
    private var fabDescOpenAnim: Animation? = null
    private var fabDescCloseAnim: Animation? = null
    private var fabCloseAnim: Animation? = null
    private var fabOpenAnim: Animation? = null
    private var isFabOpen: Boolean = true
    private var fabOriginalColor: ColorStateList? = null
    private var selectingToEdit: Boolean = false
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

        fabOpenAnim = AnimationUtils.loadAnimation(appContext,R.anim.fab_open)
        fabCloseAnim = AnimationUtils.loadAnimation(appContext,R.anim.fab_close)
        fabDescOpenAnim = AnimationUtils.loadAnimation(appContext,R.anim.fab_desc_open)
        fabDescCloseAnim = AnimationUtils.loadAnimation(appContext,R.anim.fab_desc_close)
        avisoEntradaAnim = AnimationUtils.loadAnimation(appContext,R.anim.in_topwards)
        avisoSalidaAnim = AnimationUtils.loadAnimation(appContext,R.anim.out_downwards)

        if(viewModel.isOwner) {
            FabOpen.visibility = View.VISIBLE
            FabOpen.setOnClickListener {
                toggleFloatingActionButton()
            }

            FabAddExamen.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.Action_FromVerExamenToCrearExamen)
                toggleFloatingActionButton()
            }

            fabOriginalColor = FABEditarExamen.backgroundTintList
            FABEditarExamen.setOnClickListener {
                selectingToEdit = !selectingToEdit
                if(selectingToEdit){
                    FABEditarExamen.backgroundTintList = ColorStateList.valueOf("#FFDB58".toColorInt())
                    TxtAvisoEdicion.visibility = View.VISIBLE
                    TxtAvisoEdicion.startAnimation(avisoEntradaAnim)
                }else{
                    FABEditarExamen.backgroundTintList = fabOriginalColor
                    TxtAvisoEdicion.startAnimation(avisoSalidaAnim)

                }
                toggleFloatingActionButton()
            }
            // Esconde los botones inicialmente
            toggleFloatingActionButton()
        }
    }

    private fun toggleFloatingActionButton() {

        if(isFabOpen){
            FABEditarExamen.startAnimation(fabCloseAnim)
            FabAddExamen.startAnimation(fabCloseAnim)
            TextViewEditar.startAnimation(fabDescCloseAnim)
            TextViewCrear.startAnimation(fabDescCloseAnim)
            FabOpen.setImageResource(R.drawable.ic_baseline_add_24)
            isFabOpen = false
        }else{
            FABEditarExamen.startAnimation(fabOpenAnim)
            FabAddExamen.startAnimation(fabOpenAnim)
            TextViewEditar.startAnimation(fabDescOpenAnim)
            TextViewCrear.startAnimation(fabDescOpenAnim)
            FabOpen.setImageResource(R.drawable.ic_dots)
            isFabOpen = true
        }
    }

    private fun initViewModel() {
        //viewModel = ViewModelProvider(requireActivity() as ViewModelStoreOwner).get(VerExamenesActivityViewModel::class.java)
    }

    private fun observarExamenes(){
        viewModel.obtenerExamenesObservable().observe(viewLifecycleOwner,{
            if(it == null || it.isEmpty()) {
                Toast.makeText(appContext, "No hay examenes disponibles...", Toast.LENGTH_LONG).show()
            } else{
                Log.e("Oberver","Nuevos examenes cargados")
                examenesAdapter.examenes = it.toMutableList()
                examenesAdapter.notifyDataSetChanged()
            }
        })

        viewModel.examen_publicado.observe(viewLifecycleOwner,{

            if(it != null){

                val to_edit = examenesAdapter.examenes.first{ examen -> examen.id == it.id }
                to_edit.estado = it.estado
                examenesAdapter.notifyItemChanged(examenesAdapter.examenes.indexOf(to_edit))

            }else{
                Toast.makeText(appContext, "Error en la publicación", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecyclerView(owner : String, user:String){
        recyclerViewExamenes.apply {
            layoutManager = LinearLayoutManager(appContext)
            examenesAdapter = ExamenesRecyclerViewAdapter(this@VerExamenesFragment,owner,user)
            adapter = examenesAdapter
        }
    }


    override fun onItemClick(examen: Examen) {

        Log.d("VerExamenesActivity", examen.nombre.toString())
        viewModel.selectExamen(examen.id.toString())

        if(viewModel.isOwner || viewModel.isAdmin){
            if(selectingToEdit){
                // Crear examen con examen seleccionado lo edita
                Navigation.findNavController(requireView()).navigate(R.id.Action_FromVerExamenToCrearExamen)
                selectingToEdit = false
            }else{
                Navigation.findNavController(requireView()).navigate(R.id.action_verExamenesFragment_to_evaluadosFragment)
            }
        }else{
            Navigation.findNavController(requireView()).navigate(R.id.SelectExamen)
        }

    }

    override fun setImgStatus(examen: Examen, imageView: ImageView?) {

        if (!viewModel.isOwner && !viewModel.isAdmin) return

        imageView!!.visibility = View.VISIBLE

        if (examen.estado == "creado"){

            imageView.setImageResource(R.drawable.ic_private)

            if (!viewModel.isAdmin){
                imageView.setOnClickListener {
                    publicarExamen(examen.id.toString())
                }
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

