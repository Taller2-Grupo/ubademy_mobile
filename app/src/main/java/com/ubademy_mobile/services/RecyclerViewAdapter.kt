package com.ubademy_mobile.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.tools.DownloadImageTask
import kotlinx.android.synthetic.main.course_item.view.*

class RecyclerViewAdapter(val clickListener: OnItemClickListener): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    var cursos = mutableListOf<Curso>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cursos[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemEditClick(cursos[position])
        }

        if(cursos[position].hashtags != "#prueba")
        DownloadImageTask(holder.ImgBanner).execute(cursos[position].hashtags)
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val TxtViewTitulo = view.titulo
        val ImgBanner = view.ImgBanner

        fun bind(data: Curso){
            TxtViewTitulo.text = data.titulo
        }
    }

    interface OnItemClickListener{
        fun onItemEditClick(curso: Curso)
    }

}