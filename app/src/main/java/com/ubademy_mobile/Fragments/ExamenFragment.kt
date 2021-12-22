package com.ubademy_mobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialElevationScale
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RespuestasAdapter
import com.ubademy_mobile.services.data.examenes.Consigna
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.fragment_examen.*
import kotlinx.android.synthetic.main.fragment_examen.view.*
import kotlinx.android.synthetic.main.respuesta_item.*

class ExamenFragment : Fragment(), RespuestasAdapter.OnItemClickListener {

    private var username_resuelto: String? = null
    private lateinit var respuestasAdapter : RespuestasAdapter
    val viewModel: VerExamenesActivityViewModel by activityViewModels()

    private lateinit var appActivity: FragmentActivity
    //private lateinit var viewModel: VerExamenesActivityViewModel
    private lateinit var appContext: Context
    private var id_examen_resuelto: String? = null
    private var param2: String? = null

    private lateinit var mode : ExamenMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id_examen_resuelto = it.getString("id_examen_resuelto")
            username_resuelto = it.getString("username_resuelto")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_examen, container, false)
        appActivity = requireActivity()
        appContext = requireContext()
        return view
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            ExamenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()

        initRecyclerView()
        respuestasAdapter.consignas.addAll(viewModel.examen_seleccionado.consignas!!)

        if (viewModel.isOwner || viewModel.isAdmin){
            btnEnd.text = "Enviar Correccion"
            btnEnd.setOnClickListener {

                viewModel.enviarCalificacion(id_examen_resuelto.toString())
            }
        }
        else {
            btnEnd.text = "Enviar Examen"
            btnEnd.setOnClickListener {
                viewModel.resolverExamen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getExamenResueltoPorUsuario(username_resuelto)
    }
    private fun initRecyclerView(){
        recyclerRespuestas.apply {
            layoutManager = LinearLayoutManager(appContext)
            respuestasAdapter = RespuestasAdapter(this@ExamenFragment, appContext,viewModel.isOwner || viewModel.isAdmin)
            adapter = respuestasAdapter
        }}

    private fun initViewModel() {

        //viewModel = ViewModelProvider(context as ViewModelStoreOwner).get(VerExamenesActivityViewModel::class.java)
        viewModel.examen_seleccionado.apply {
            if (this.id == null) {
                Toast.makeText(appContext, "No se ha seleccioado un examen", Toast.LENGTH_LONG)
                    .show()
            } else {
                Log.d("Examen fragment", "Seleccionado => ${this.nombre} { id: ${this.id}}")
                TxTNombreExamen.text = this.nombre

            }
        }

        viewModel.examen_resuelto.observe(viewLifecycleOwner) {

            if (it?.id != null) {

                viewModel.examen_seleccionado.consignas?.forEach { consigna ->
                    it.respuestas.forEach { respuesta ->
                        if (respuesta.id_consigna == consigna.id)
                            consigna.estadoUser = respuesta.estado
                    }
                }
                id_examen_resuelto = it.id
                Log.e("Resuelto encontrado", "ID: ${it.id}")

                // Si es admin y todavia no corrigio el examen
                if((viewModel.isOwner || viewModel.isAdmin ) && it.corrector == null  ) {
                    btnEnd.visibility = View.VISIBLE
                }else{
                    btnEnd.visibility = View.GONE
                }

            } else {
                viewModel.examen_seleccionado.consignas?.forEach { consigna ->
                    consigna.estadoUser = "Pendiente"
                }
                btnEnd.visibility = View.VISIBLE
            }
            respuestasAdapter.consignas =
                (viewModel.examen_seleccionado.consignas!!).toMutableList()
            respuestasAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemEditClick(consigna: Consigna, view: View,idx: Int) {

        exitTransition= MaterialElevationScale(false).apply{
            duration = (500).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = (500).toLong()
        }

        //val name = getString(R.string.consignas_transition_name)
        //val extras = FragmentNavigatorExtras(view to name)
        //val direction = ExamenFragmentDirections.actionExamenToConsigna()

        val bundle = bundleOf(
            "idx_consigna" to idx.toString(),
            "id_examen_resuelto" to id_examen_resuelto)

        findNavController().navigate(R.id.actionExamenToConsigna,bundle)

    }
}