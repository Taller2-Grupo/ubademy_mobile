package com.ubademy_mobile.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.Item
import kotlinx.android.synthetic.main.activity_subir_archivos.*
import kotlinx.android.synthetic.main.activity_visualizar_imagenes.*
import java.util.*

class VisualizarImagenesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_imagenes)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("User")
        val imageList: ArrayList<Item> = ArrayList()
        progressBar.visibility = View.VISIBLE

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    Log.d("item", "$it")
                    imageList.add(Item(it.toString()))
                }.addOnCompleteListener {
                    recyclerView.adapter = ImageAdapter(imageList, this)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

}