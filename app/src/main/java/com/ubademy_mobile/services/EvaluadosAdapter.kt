package com.ubademy_mobile.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.examenes.ExamenResuelto
import kotlinx.android.synthetic.main.evaluado_item.view.*

class EvaluadosAdapter( val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<EvaluadosAdapter.ViewHolder>() {

    var evaluados = mutableListOf<ExamenResuelto>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val TxtNombreEvaluado = view.TxtNombreEvaluado
        val checkboxEstado = view.checkboxEstado
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.evaluado_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.checkboxEstado.isChecked = evaluados[position].estado == "corregido"
        viewHolder.TxtNombreEvaluado.text = evaluados[position].cursada!!.username
        viewHolder.itemView.setOnClickListener{
            clickListener.onItemClick(evaluados[position],it)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = evaluados.size

    interface OnItemClickListener{
        fun onItemClick(resuelto: ExamenResuelto, view: View)
    }
}
