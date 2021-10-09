package com.ubademy_mobile.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.R
import kotlinx.android.synthetic.main.recycler_row_list.view.*

class RecyclerViewAdapter(val clickListener: OnItemClickListener): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    var cursos = mutableListOf<Curso>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_list, parent, false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.bind(cursos[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemEditClick(cursos[position])
        }
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val TxtViewTitulo = view.TxtViewTitulo
        val TxtViewDescripcion = view.TxtViewDescripcion
        val TxtViewEstado = view.TxtViewEstado
        fun bind(data: Curso){
            TxtViewTitulo.text = data.titulo
            TxtViewDescripcion.text = data.descripcion
            TxtViewEstado.text = data.estado
        }
    }

    interface OnItemClickListener{
        fun onItemEditClick(curso: Curso)
    }

}