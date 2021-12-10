package com.ubademy_mobile.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.ubademy_mobile.services.ListadoArchivosAdapter
import kotlinx.android.synthetic.main.activity_subir_archivos.*
import java.util.*

class SubirArchivosActivity : AppCompatActivity(), ListadoArchivosAdapter.fileListener {

    private val fileResult = 1

    private val database = Firebase.database

    lateinit var recyclerViewAdapter: ListadoArchivosAdapter

    //private val myRef = database.getReference(intent.getStringExtra("CursoId"))

    private fun initRecyclerView(){

        val cursoId = intent.getStringExtra("CursoId")
        if (cursoId == null) return;
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(cursoId)
        //progressBar.visibility = View.VISIBLE

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SubirArchivosActivity)
            val decoration = DividerItemDecoration(this@SubirArchivosActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = ListadoArchivosAdapter(this@SubirArchivosActivity)
            adapter = recyclerViewAdapter
        }

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            //progressBar.visibility = View.GONE

            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    val file: ListadoArchivosAdapter.File = ListadoArchivosAdapter.File(name = item.name, url = it.toString())
                    recyclerViewAdapter.files.add(file)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_archivos)

        uploadImageView.setOnClickListener {
            fileManager()
        }

        siguiente.setOnClickListener{
            finish()
        }

        initRecyclerView()
    }

    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        intent.type = "*/*"
        startActivityForResult(intent, fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult) {
            if (resultCode == RESULT_OK && data != null) {

                val clipData = data.clipData

                if (clipData != null){
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        uri?.let { fileUpload(it) }
                    }
                }else {
                    Log.e("clipdata", data.dataString.toString())
                    val uri = data.data
                    uri?.let { fileUpload(it) }
                }
            }
        }
    }

    private fun fileUpload(mUri: Uri) {
        val cursoId = intent.getStringExtra("CursoId")
        if (cursoId == null) return;
        val myRef = database.getReference(cursoId)
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child(cursoId)
        val path = mUri.lastPathSegment.toString()
        val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/')+1))

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->
                val file: ListadoArchivosAdapter.File = ListadoArchivosAdapter.File(name = mUri.lastPathSegment, url = uri.toString())
                if (!recyclerViewAdapter.files.contains(file)) {
                    val hashMap = HashMap<String, String>()
                    hashMap["link"] = java.lang.String.valueOf(uri)

                    myRef.child(myRef.push().key.toString()).setValue(hashMap)

                    Log.e("file", "archivo nuevo")
                    recyclerViewAdapter.files.add(file)
                    //recyclerViewAdapter.notifyItemChanged(recyclerViewAdapter.files.size)
                    recyclerViewAdapter.notifyDataSetChanged()

                    Log.e("message", mUri.lastPathSegment.toString() + " - " + uri.toString())
                }
            }
        }.addOnFailureListener {
            Log.i("message", "file upload error")
        }
    }

    override fun onItemEditClick(file: ListadoArchivosAdapter.File) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(file.url)
        startActivity(openURL)
    }
    override fun onItemDelete(file: ListadoArchivosAdapter.File){
        val cursoId = intent.getStringExtra("CursoId")
        if (cursoId == null) return;
        var imgRef = FirebaseStorage.getInstance().reference.child(cursoId+"/"+file.name)
        imgRef.delete().addOnSuccessListener {
            Log.e("delete", file.name + " deleted correctly")
        }.addOnFailureListener {
            Log.e("delete failed", file.name + " not deleted")
        }
    }

}