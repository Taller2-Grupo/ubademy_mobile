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
import com.ubademy_mobile.activities.ExamenesActivity
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.fragment_consgina.*

class ConsginaFragment : Fragment() {

    private var last_consigna: Boolean = false
    private lateinit var appContext: Context
    val viewModel: VerExamenesActivityViewModel by activityViewModels()
    //val args: ConsignaFragmentArgs by navArgs()

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

        viewModel.examen_seleccionado.consignas!!.size.apply {
            if (idx_consigna >= this )
                idx_consigna = this - 1
            else if( idx_consigna == this - 1)
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

        if(viewModel.isOwner){
            setBotoneraAdmin()
        }else{
            setBotoneraUser()
        }

        setConsigna()
    }

    private fun setConsigna() {
        TxTEnunciado.text = viewModel.examen_seleccionado.consignas?.get(idx_consigna)?.enunciado.toString()
    }

    fun setBotoneraAdmin(){

        BtnDesaprobar.visibility = View.VISIBLE
        BtnAprobar.visibility = View.VISIBLE
        BtnSiguiente.visibility = View.GONE

        BtnDesaprobar.setOnClickListener {
            desaprobar()
        }

        BtnAprobar.setOnClickListener {
            aprobar()
        }
    }

    fun setBotoneraUser(){

        if(last_consigna){

            BtnSiguiente.text = "Finalizar"
            BtnSiguiente.setBackgroundColor("#4CBA4C".toColorInt())
            BtnSiguiente.setOnClickListener {
                viewModel.agregarRespuesta(idx_consigna,txtInputRespuesta.editText?.text.toString())
                viewModel.resolverExamen()
                finalizarExamen()
            }
        }else{
            BtnSiguiente.visibility = View.VISIBLE
            BtnSiguiente.setOnClickListener {

                viewModel.agregarRespuesta(idx_consigna,txtInputRespuesta.editText?.text.toString())
                siguienteConsigna()
            }
        }
    }

    private fun finalizarExamen() {

        findNavController().navigate(R.id.ActionConsginaToExamen)
    }

    private fun aprobar() {
        Log.e("Aprobar","Aproba2")
    }

    private fun desaprobar() {
        Log.e("Desaprobado","chau pt")
    }


    private fun siguienteConsigna() {

        val bundle = bundleOf("idx_consigna" to (idx_consigna+1).toString())
        findNavController().navigate(R.id.action_SiguienteConsigna, bundle)
    }
}