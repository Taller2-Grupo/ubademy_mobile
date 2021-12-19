package com.ubademy_mobile.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.EvaluadosAdapter
import com.ubademy_mobile.services.data.examenes.ExamenResuelto
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.evaluados_fragment.*

class EvaluadosFragment : Fragment(), EvaluadosAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = EvaluadosFragment()
    }

    private lateinit var evaluadosAdapter: EvaluadosAdapter
    private lateinit var appContext: Context
    //private lateinit var viewModel: EvaluadosViewModel
    val viewModel : VerExamenesActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        appContext = requireContext()
        return inflater.inflate(R.layout.evaluados_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initViewModel()

        viewModel.getExamenesResueltos()

    }

    private fun initRecyclerView(){
        ListaDeAlumnos.apply {
            layoutManager = LinearLayoutManager(appContext)
            evaluadosAdapter = EvaluadosAdapter(this@EvaluadosFragment)
            adapter = evaluadosAdapter
        }}

    private fun initViewModel(){

        viewModel.examenes_resueltos.observe(viewLifecycleOwner,{
            evaluadosAdapter.evaluados = it.toMutableList()
            evaluadosAdapter.notifyDataSetChanged()
        })
    }
    override fun onItemClick(resuelto: ExamenResuelto, view: View) {

        // Inicializa la estructura para comenzar a corregir
        viewModel.seleccionarResuelto(resuelto)

        // Navegar al siguiente fragmento
        val bundle = bundleOf(
            "idx_consigna" to 0,
            "id_examen_resuelto" to resuelto.id)

        findNavController().navigate(R.id.action_evaluadosFragment_to_NaVexamenFragment,bundle)
    }

}