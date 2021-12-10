package com.ubademy_mobile.services

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.ImageAdapter
import com.ubademy_mobile.services.data.Item
import kotlinx.android.synthetic.main.activity_visualizar_imagenes.*
import kotlinx.android.synthetic.main.file_item.view.*
import java.util.ArrayList

class VisualizacionArchivosAdapter(val clickListener: fileListener): RecyclerView.Adapter<VisualizacionArchivosAdapter.MyViewHolder>() {

    data class File (
        val name: String?  = null ,
        val url: String? = null
    )

    var files = mutableListOf<File>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.file_visualization_item, parent, false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(files[position])
        holder.itemView.setOnClickListener {
            clickListener.onItemEditClick(files[position])
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val TxtViewFileName = view.fileName

        fun bind(data: File){
            TxtViewFileName.text = data.name
        }
    }

    interface fileListener{
        fun onItemEditClick(file: File)
    }

}