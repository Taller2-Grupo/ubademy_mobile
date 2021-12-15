package com.ubademy_mobile.services

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.Fragments.ExamenMode
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.examenes.Consigna
import com.ubademy_mobile.services.data.examenes.CorreccionRequest
import kotlinx.android.synthetic.main.respuesta_item.*
import kotlinx.android.synthetic.main.respuesta_item.view.*

class RespuestasAdapter(
    val clickListener: RespuestasAdapter.OnItemClickListener,
    val context: Context
) :
    RecyclerView.Adapter<RespuestasAdapter.ViewHolder>() {

    var consignas = mutableListOf<Consigna>()
    val correcciones = mutableListOf<CorreccionRequest>()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val TxTEnunciado = view.TxTEnunciado
        val TxTEstado = view.TxTEstado
        val ImgStatus = view.ImgStatus
        //val txtInputRespuesta = view.txtInputRespuesta
        //val correctorContainer = view.correctorContainer
        //val corrector = view.corrector
        //val calificacionContainer = view.calificacionContainer
        val TxtCalificacion = view.TXTcalificacion
        val TxtCalificacionMaxima = view.TXTcalificacionMaxima
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

        var drawable : Drawable? = context.getDrawable(R.drawable.ic_warning_circle)
        var calificacion = "-"

        when (consignas[position].estadoUser){
            "sin corregir" -> {
                drawable = context.getDrawable(R.drawable.ic_checked)
                calificacion = "-"
            }
            "correcta" -> {
                drawable = context.getDrawable(R.drawable.ic_checked_green)
                calificacion = consignas[position].puntaje.toString()
            }
            "incorrecta" -> {
                drawable = context.getDrawable(R.drawable.ic_wrong)
                calificacion = "0"
            }
        }

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.TxTEnunciado.text = consignas[position].enunciado
        viewHolder.TxTEstado.text = consignas[position].estadoUser
        viewHolder.TxtCalificacionMaxima.text = consignas[position].puntaje
        viewHolder.ImgStatus.setImageDrawable(drawable)
        viewHolder.TxtCalificacion.text = calificacion
        viewHolder.itemView.setOnClickListener{
            clickListener.onItemEditClick(consignas[position],it,position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = consignas.size

    interface OnItemClickListener{
        fun onItemEditClick(consigna: Consigna, view: View, idx: Int)
    }
}