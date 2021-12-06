package com.ubademy_mobile.services

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.Examen
import kotlinx.android.synthetic.main.recycler_examenes_list.view.*

class ExamenesRecyclerViewAdapter(
    val clickListener: OnItemClickListener,
    val idCreador: String,
    val idUsuario: String) :
    RecyclerView.Adapter<ExamenesRecyclerViewAdapter.ViewHolder>() {

    var examenes = mutableListOf<Examen>()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val TxtExamen = view.TxtViewNombre
            get() = field
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_examenes_list, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Log.e("Logger","${examenes[position]} vs ( ${idCreador} and ${idUsuario} )")
        var name = examenes[position]

        viewHolder.TxtExamen.text = name.nombre

        viewHolder.itemView.setOnClickListener {
            clickListener.onItemClick(examenes[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = examenes.size

    interface OnItemClickListener{

        fun onItemClick(examen: Examen)
    }
}