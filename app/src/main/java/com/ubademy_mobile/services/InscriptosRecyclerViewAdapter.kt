package com.ubademy_mobile.services

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.R
import kotlinx.android.synthetic.main.recycler_inscriptos_list.view.*

class InscriptosRecyclerViewAdapter(
    val clickListener: OnItemClickListener,
    val idCreador: String,
    val idUsuario: String) :
    RecyclerView.Adapter<InscriptosRecyclerViewAdapter.ViewHolder>() {

    var inscriptos = mutableListOf<String>()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val TxtInscripto = view.TxtViewNombre
            get() = field
        val ImgOwner = view.ImgOwner
            get() = field
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_inscriptos_list, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Log.e("Logger","${inscriptos[position]} vs ( ${idCreador} and ${idUsuario} )")
        var name = inscriptos[position]

        if(inscriptos[position] == idCreador){
            viewHolder.ImgOwner.visibility = View.VISIBLE
        }

        if (inscriptos[position] == idUsuario){
            name += " (tú)"
        }

        viewHolder.TxtInscripto.text = name

        viewHolder.itemView.setOnClickListener {
            clickListener.onItemClick(inscriptos[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = inscriptos.size

    interface OnItemClickListener{
        fun onItemClick(inscripto: String)
    }
}
