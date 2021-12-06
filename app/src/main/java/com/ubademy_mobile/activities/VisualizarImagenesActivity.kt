package com.ubademy_mobile.activities

/*import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.Item
import kotlinx.android.synthetic.main.activity_visualizar_imagenes.*
import java.util.*

class VisualizarImagenesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_imagenes)

        var idCurso = intent.getStringExtra("cursoId").toString()


        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(idCurso)
        val imageList: ArrayList<Item> = ArrayList()
        progressBar.visibility = View.VISIBLE

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            progressBar.visibility = View.GONE

            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                //item.name
                item.downloadUrl.addOnSuccessListener {
                    Log.d("item", "$it")
                    imageList.add(Item(it.toString()))
                }.addOnCompleteListener {
                    recyclerView.adapter = ImageAdapter(imageList, this)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                }
            }

            if(items.isEmpty()) showEmptyAlert()
            else hideEmptyAlert()
        }
    }

    private fun hideEmptyAlert() {
        TxtSinImagenes.visibility = View.GONE
    }

    private fun showEmptyAlert() {
        TxtSinImagenes.visibility = View.VISIBLE
    }

}*/

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.ubademy_mobile.R
import com.ubademy_mobile.services.VisualizacionArchivosAdapter
import kotlinx.android.synthetic.main.activity_visualizar_imagenes.*
import java.util.*

class VisualizarImagenesActivity : AppCompatActivity(), VisualizacionArchivosAdapter.fileListener {

    private val fileResult = 1

    private val database = Firebase.database

    lateinit var recyclerViewAdapter: VisualizacionArchivosAdapter

    //private val myRef = database.getReference(intent.getStringExtra("CursoId"))

    private fun initRecyclerView(){

        val cursoId = intent.getStringExtra("cursoId")
        if (cursoId == null) return;
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(cursoId)
        progressBar.visibility = View.VISIBLE

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VisualizarImagenesActivity)
            val decoration = DividerItemDecoration(this@VisualizarImagenesActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = VisualizacionArchivosAdapter(this@VisualizarImagenesActivity)
            adapter = recyclerViewAdapter
        }

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            progressBar.visibility = View.GONE

            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    val file: VisualizacionArchivosAdapter.File = VisualizacionArchivosAdapter.File(name = item.name, url = it.toString())
                    recyclerViewAdapter.files.add(file)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_imagenes)

        initRecyclerView()
    }

    override fun onItemEditClick(file: VisualizacionArchivosAdapter.File) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(file.url)
        startActivity(openURL)
    }

}