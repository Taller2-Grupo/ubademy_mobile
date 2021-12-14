package com.ubademy_mobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.ubademy_mobile.R
import com.ubademy_mobile.services.ConsignaAdapter
import com.ubademy_mobile.services.data.examenes.Consigna
import com.ubademy_mobile.services.data.examenes.Examen
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.consigna_item.view.*
import kotlinx.android.synthetic.main.fragment_crear_examen.*



class CrearExamenFragment : Fragment(), ConsignaAdapter.OnItemClickListener {

    private var editMode: Boolean = false
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

        if( viewModel.examen_seleccionado.id != null){
            editMode = true
        }

        BtnCrearExamen.apply {

            if (editMode) {
                this.text = "Editar"
                this.setOnClickListener { editarExamen() }
            }else{
                this.text = "Crear"
                this.setOnClickListener {  crearExamen() }
            }
        }

        BtnNuevaConsigna.setOnClickListener {
            nuevaConsigna()
        }

        initRecyclerView("owner","user")
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.nuevo_examen.observe(viewLifecycleOwner,{

            if (it?.id != null){
                Log.e("examen","${it.consignas} - ${it.nombre}")
                Toast.makeText(appContext,"Operacion exitosa. Examen id ${it.id}",Toast.LENGTH_LONG).show()
                Navigation.findNavController(requireView()).navigate(R.id.ActionNewExamenDone)
                viewModel.nuevo_examen.value = Examen()
            }else{
                Toast.makeText(appContext,"Error en la creacion",Toast.LENGTH_LONG).show()
            }
        })
        viewModel.examen_seleccionado.apply {
            if(this.id != null){
                TxTNombreExamen.editText!!.setText(this.nombre)
                this.consignas?.let { consignaAdapter.consignas.addAll(it) }
            }
        }
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

    private fun editarExamen(){
        if(validar_formulario()){

            viewModel.editarExamenSeleccionado(
                nombre = TxTNombreExamen.editText!!.text.toString(),
                consignas = consignaAdapter.consignas
            )
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

    /*
    fun eliminarExamen(id_examen : String){
        val alert: AlertDialog.Builder = AlertDialog.Builder(appContext)
        alert.setTitle("Eliminar")
        alert.setMessage("¿Desea eliminar el examen?")
        alert.setPositiveButton("Sí") { dialog, _ ->

            viewModel.eliminarExamen(examen_id = id_examen)
            dialog.dismiss()
        }

        alert.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        alert.show()
    }
    */
}