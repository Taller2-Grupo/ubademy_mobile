package com.ubademy_mobile.services

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.Usuario
import kotlinx.android.synthetic.main.recycler_inscriptos_list.view.*

class InscriptosRecyclerViewAdapter(
    val clickListener: OnItemClickListener,
    val idCreador: String,
    val idUsuario: String) :
    RecyclerView.Adapter<InscriptosRecyclerViewAdapter.ViewHolder>() {

    var inscriptos = mutableListOf<Usuario>()
    var colaboradores = mutableListOf<String>()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val TxtInscripto = view.TxtViewNombre
            get() = field
        val ImgView = view.ImgView
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
        var name = inscriptos[position].nombre?.capitalize() + " " + inscriptos[position].apellido?.capitalize()

        if(inscriptos[position].username == idCreador){
            viewHolder.ImgView.visibility = View.VISIBLE
            viewHolder.ImgView.background = null
        }else{

            if(idCreador == idUsuario){
                if(colaboradores.contains(inscriptos[position].username)){
                    viewHolder.ImgView.visibility = View.VISIBLE
                    viewHolder.ImgView.setImageResource(R.drawable.ic_baseline_person_add_disabled_24)
                    viewHolder.ImgView.background = null
                    viewHolder.ImgView.setOnClickListener{
                        clickListener.eliminarColaborador(inscriptos[position])
                    }
                }else{
                    viewHolder.ImgView.visibility = View.VISIBLE
                    viewHolder.ImgView.setImageResource(R.drawable.ic_baseline_person_add_24)
                    viewHolder.ImgView.background = null
                    viewHolder.ImgView.setOnClickListener{
                        clickListener.agregarColaborador(inscriptos[position])
                    }
                }
            }else{
                if(colaboradores.contains(inscriptos[position].username)){

                    viewHolder.ImgView.visibility = View.VISIBLE
                    viewHolder.ImgView.setImageResource(R.drawable.ico_colaboradores)
                    viewHolder.ImgView.setBackgroundResource(R.drawable.button_rounded)
                    viewHolder.ImgView.setOnClickListener{ }
                }else{
                    viewHolder.ImgView.visibility = View.GONE
                    viewHolder.ImgView.setOnClickListener{ }
                }

            }
        }


        if (inscriptos[position].username == idUsuario){
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
        fun onItemClick(inscripto: Usuario)
        fun eliminarColaborador(usuario: Usuario)
        fun agregarColaborador(usuario: Usuario)
    }
}
