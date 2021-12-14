package com.ubademy_mobile.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.Fragments.ExamenMode
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.examenes.Consigna
import com.ubademy_mobile.services.data.examenes.CorreccionRequest
import kotlinx.android.synthetic.main.respuesta_item.view.*

class RespuestasAdapter(
    val clickListener: RespuestasAdapter.OnItemClickListener,
    val mode: ExamenMode = ExamenMode.RESOLUCION) :
    RecyclerView.Adapter<RespuestasAdapter.ViewHolder>() {

    var consignas = mutableListOf<Consigna>()
    val correcciones = mutableListOf<CorreccionRequest>()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val TxTEnunciado = view.TxTEnunciado
            get() = field
        //val txtInputRespuesta = view.txtInputRespuesta
        //val correctorContainer = view.correctorContainer
        //val corrector = view.corrector
        //val calificacionContainer = view.calificacionContainer
        //val calificacion = view.calificacion
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.respuesta_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.TxTEnunciado.text = consignas[position].enunciado
        viewHolder.itemView.setOnClickListener{
            clickListener.onItemEditClick(consignas[position],it,position)
        }

        when(mode){

            ExamenMode.RESOLUCION -> {
                viewHolder.TxTEnunciado.text = consignas[position].enunciado

                //viewHolder.calificacionContainer.visibility = View.GONE
                //viewHolder.correctorContainer.visibility = View.GONE
            }
            ExamenMode.CORRECCION -> {
                //viewHolder.correctorContainer.visibility = View.GONE
                //viewHolder.txtInputRespuesta.editText!!.isEnabled = false
            }
            ExamenMode.REVISION -> {
                //viewHolder.txtInputRespuesta.editText!!.isEnabled = false
                //viewHolder.calificacion.isEnabled = false
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = consignas.size

    interface OnItemClickListener{
        fun onItemEditClick(consigna: Consigna, view: View, idx: Int)
    }
}