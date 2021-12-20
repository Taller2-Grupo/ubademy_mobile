package com.ubademy_mobile.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.ubademy_mobile.R
import com.ubademy_mobile.services.InscriptosRecyclerViewAdapter
import com.ubademy_mobile.services.data.examenes.ExamenResuelto
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.fragment_consgina.*

class ConsginaFragment : Fragment() {

    private var examenResuelto: ExamenResuelto? = null
    private var id_examen_resuelto: String? = null
    private var userTarget: String = ""
    private var last_consigna: Boolean = false
    private lateinit var appContext: Context
    val viewModel: VerExamenesActivityViewModel by activityViewModels()
    //val args: ConsignaFragmentArgs by navArgs()

    private lateinit var inscriptosAdapter : InscriptosRecyclerViewAdapter
    private var idx_consigna : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply{
            drawingViewId = R.id.NavHostFragment
            duration = (1000).toLong()
            scrimColor  = Color.TRANSPARENT
            //setAllContainerColors(R.attr.colorSurface)

        }

        idx_consigna = arguments?.getString("idx_consigna")!!.toInt()
        id_examen_resuelto = arguments?.getString("id_examen_resuelto")

        if(!id_examen_resuelto.isNullOrEmpty()){
            Log.e("Resuelto encontrado", "ID: ${id_examen_resuelto}")
        }

        val size = viewModel.examen_seleccionado.consignas!!.size
        if ( idx_consigna >= size - 1 ){
            idx_consigna = size - 1
            last_consigna = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_consgina, container, false)

        // Get appcontext
        appContext = requireContext()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initViewModel()

        if(!id_examen_resuelto.isNullOrEmpty()) {
            examenResuelto =
                viewModel.examenes_resueltos.value?.first { it.id == id_examen_resuelto }
            if (examenResuelto != null){
                setViewExamenResuelto()
            }else{
                viewModel.getExamenResueltoPorUsuario()
            }

        }

        if(viewModel.isOwner){
            txtInputRespuesta.editText!!.isEnabled = false
            setBotoneraAdmin()
        }else{
            setBotoneraUser()
        }

        // viewModel.resolverExamen()

        setConsigna()
        setRespuesta()
    }

    fun initViewModel(){
        viewModel.examen_resuelto.observe(viewLifecycleOwner,{

            it?.apply {
                examenResuelto= it
                setViewExamenResuelto()
            }
        })
    }

    private fun setRespuesta() {
        txtInputRespuesta.editText!!.apply {
            viewModel.examen_resuelto.value?.respuestas?.forEach{
                if (it.id_consigna == viewModel.examen_seleccionado.consignas?.get(idx_consigna)?.id)
                    this.setText(it.resolucion.toString())
                    this.isEnabled = false
            }
        }
    }

    private fun setConsigna() {
        TxTEnunciado.text = viewModel.examen_seleccionado.consignas?.get(idx_consigna)?.enunciado.toString()
    }

    fun setBotoneraAdmin(){

        botoneraAdmin.visibility = View.VISIBLE

        BtnDesaprobar.setOnClickListener {

            viewModel.calificarRespuesta(idx_consigna, false)
            BtnAprobar.isEnabled = false
        }

        BtnAprobar.setOnClickListener {

            viewModel.calificarRespuesta(idx_consigna, true)
            BtnDesaprobar.isEnabled = false
        }

        BtnSiguiente.setOnClickListener { siguienteConsigna()}

        if(last_consigna){

            BtnSiguiente.text = "Enviar corrección"
            BtnSiguiente.setBackgroundColor("#4CBA4C".toColorInt())
            BtnSiguiente.setOnClickListener {
                //viewModel.enviarCalificacion(id_examen_resuelto!!)
                finalizarExamen()
            }
        }
    }

    fun setBotoneraUser(){

        botoneraAdmin.visibility = View.GONE

        if(last_consigna){

            //BtnSiguiente.text = "Finalizar"
            BtnSiguiente.setBackgroundColor("#4CBA4C".toColorInt())
            BtnSiguiente.setOnClickListener {

                if(examenResuelto == null) {
                    viewModel.agregarRespuesta(
                        idx_consigna,
                        txtInputRespuesta.editText?.text.toString()
                    )
                    //viewModel.resolverExamen()
                }

                finalizarExamen()
            }
        }else{
            BtnSiguiente.visibility = View.VISIBLE
            BtnSiguiente.setOnClickListener {

                if(examenResuelto == null) {
                    viewModel.agregarRespuesta(
                        idx_consigna,
                        txtInputRespuesta.editText?.text.toString()
                    )
                }
                siguienteConsigna()
            }
        }
    }

    private fun finalizarExamen() {

        findNavController().popBackStack(R.id.NaVexamenFragment,false)
    }



    private fun siguienteConsigna() {

        val bundle = bundleOf(
            "idx_consigna" to (idx_consigna+1).toString(),
            "id_examen_resuelto" to id_examen_resuelto)

        findNavController().navigate(R.id.action_SiguienteConsigna, bundle)
    }

    fun setViewExamenResuelto() {

        val respuesta = examenResuelto!!.respuestas.first{
            it.id_consigna == viewModel.examen_seleccionado.consignas?.get(idx_consigna)?.id
        }

        userTarget = examenResuelto?.cursada?.username.toString()
        txtInputRespuesta.hint = "Respuesta de $userTarget"
        txtInputRespuesta.editText!!.setText(respuesta.resolucion.toString())
        corrector.text = if (examenResuelto?.corrector != null) examenResuelto?.corrector else "No corregido"
        Log.e("ESTADO resupuesta ", "${respuesta.estado}")
        when(respuesta.estado){
            "correcta" -> {
                calificacion.text = viewModel.examen_seleccionado.consignas
                    ?.get(idx_consigna)
                    ?.puntaje.toString()
                BtnDesaprobar.isEnabled = true
            }
            "incorrecta" -> {
                calificacion.text = "0"
                BtnAprobar.isEnabled = false
            }
        }
    }
}