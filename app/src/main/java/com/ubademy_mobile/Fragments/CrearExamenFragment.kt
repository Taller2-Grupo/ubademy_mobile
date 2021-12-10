package com.ubademy_mobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.ubademy_mobile.R
import com.ubademy_mobile.services.ConsignaAdapter
import com.ubademy_mobile.services.ExamenesRecyclerViewAdapter
import com.ubademy_mobile.services.data.Consigna
import com.ubademy_mobile.services.data.Examen
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_examenes.*
import kotlinx.android.synthetic.main.consigna_item.view.*
import kotlinx.android.synthetic.main.fragment_crear_examen.*

class CrearExamenFragment : Fragment(), ConsignaAdapter.OnItemClickListener {

    private lateinit var appContext: Context
    private lateinit var consignaAdapter: ConsignaAdapter
    val viewModel: VerExamenesActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_crear_examen, container, false)

        // Get Context
        appContext = requireContext()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        BtnCrearExamen.setOnClickListener {
            crearExamen()
        }

        BtnNuevaConsigna.setOnClickListener {
            nuevaConsigna()
        }

        initViewModel()
        initRecyclerView("owner","user")
    }

    private fun initViewModel() {
        viewModel.examen_seleccionado.observe(viewLifecycleOwner,{
            if (it != null){
                Toast.makeText(appContext,"Examen creado exitosamente con id ${it.id}",Toast.LENGTH_LONG).show()
                Navigation.findNavController(requireView()).navigate(R.id.ActionNewExamenDone)
            }else{
                Toast.makeText(appContext,"Error en la creacion",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecyclerView(owner : String, user:String){
        RecyclerConsignas.apply {
            layoutManager = LinearLayoutManager(appContext)
            consignaAdapter = ConsignaAdapter(this@CrearExamenFragment,owner,user)
            adapter = consignaAdapter
        }}

    private fun nuevaConsigna() {

        consignaAdapter.consignas.add(Consigna())
        consignaAdapter.notifyItemInserted(consignaAdapter.consignas.size-1)
    }

    private fun crearExamen() {

        if(validar_formulario()){

            val new_Examen = Examen(
                nombre = TxTNombreExamen.editText!!.text.toString(),
                id_curso = viewModel.idcurso,
                consignas = consignaAdapter.consignas
            )

            viewModel.crearExamen(new_Examen)
        }

    }

    private fun validar_formulario(): Boolean {

        var result = true

        if(TxTNombreExamen.editText?.text!!.isEmpty()){
            TxTNombreExamen.error="Campo obligatorio"
            TxTNombreExamen.isErrorEnabled = true
            result = false
        }else{
            TxTNombreExamen.isErrorEnabled = false
        }

        return result
    }

    override fun onItemClick(consigna: Consigna) {


    }

    override fun onValidate(consigna: Consigna, txtField: TextInputLayout) {

        if(txtField.hint == "Consigna"){
            if (txtField.hint == "Consgina"){
                if(txtField.editText?.text!!.isEmpty()){
                    txtField.error="Campo obligatorio"
                    txtField.isErrorEnabled = true
                }else{
                    txtField.isErrorEnabled = false
                }
            }
        }else{

            if(txtField.editText?.text!!.isEmpty() ){
                txtField.error="Campo obligatorio"
                txtField.isErrorEnabled = true
            }else if (txtField.editText?.text.toString().toInt() !in 1..10){
                txtField.error="Ingresa un numero entero del 1 al 10"
                txtField.isErrorEnabled = true
            }else {
                txtField.isErrorEnabled = false
            }
        }
    }

}