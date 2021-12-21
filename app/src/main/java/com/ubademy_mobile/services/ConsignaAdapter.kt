package com.ubademy_mobile.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.examenes.Consigna
import kotlinx.android.synthetic.main.consigna_item.view.*

class ConsignaAdapter(
    val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<ConsignaAdapter.ViewHolder>() {

    var consignas = mutableListOf<Consigna>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val TxtTitulo = view.TxtTitulo
            get() = field
        val TxtEnunciado = view.TxtFieldConsigna
            get() = field
        val TxtPuntaje= view.TxtFieldPuntaje
            get() = field
        val BtnRemove = view.BtnRemove
            get() = field
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.consigna_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        //viewHolder.TxtEnunciado.editText?.setText(consignas[position].enunciado)

        viewHolder.itemView.setOnClickListener {
            clickListener.onItemClick(consignas[viewHolder.position])
        }

        viewHolder.TxtTitulo.setText("${viewHolder.position+1}º Consigna")
        viewHolder.BtnRemove.setOnClickListener {
            consignas.removeAt(viewHolder.position)
            this@ConsignaAdapter.notifyDataSetChanged()
        }

        viewHolder.TxtEnunciado.editText!!.setText(consignas[position].enunciado)
        viewHolder.TxtPuntaje.editText!!.setText(consignas[position].puntaje)

        viewHolder.TxtEnunciado.editText!!.addTextChangedListener {
            consignas[position].enunciado = it.toString()
            clickListener.onValidate(consignas[position],viewHolder.TxtEnunciado)
        }

        viewHolder.TxtPuntaje.editText!!.addTextChangedListener {
            consignas[position].puntaje = it.toString()
            clickListener.onValidate(consignas[position],viewHolder.TxtPuntaje)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = consignas.size

    interface OnItemClickListener{

        fun onItemClick(consigna: Consigna)
        fun onValidate(consigna: Consigna, txtField: TextInputLayout )
    }
}