package com.ubademy_mobile.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubademy_mobile.R
import com.ubademy_mobile.services.InscriptosRecyclerViewAdapter
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.view_models.VerInscriptosActivityViewModel
import kotlinx.android.synthetic.main.activity_ver_inscriptos.*
import kotlinx.android.synthetic.main.user_item.*

class VerInscriptosActivity : AppCompatActivity(), InscriptosRecyclerViewAdapter.OnItemClickListener {

    private lateinit var idCurso: String
    private lateinit var viewModel: VerInscriptosActivityViewModel
    private lateinit var inscriptosAdapter: InscriptosRecyclerViewAdapter
    private lateinit var ownerUsername : String
    private lateinit var owner : Usuario
    private lateinit var colabUsernames : ArrayList<String>
    private lateinit var colaboradores : MutableList<Usuario>
    private lateinit var inscriptosUsernames : Array<String>
    private lateinit var inscriptos : MutableList<Usuario>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_inscriptos)

        idCurso = intent.getStringExtra("cursoId").toString()
        ownerUsername = intent.getStringExtra("ownerId").toString()
        colabUsernames = intent.getStringArrayListExtra("colaboradores") as ArrayList<String>

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", null)


        if(user != null) initRecyclerView(ownerUsername,user)
        else Log.e("VerInscriptosActivity", "Usuario no logueado")

        initViewModel()
        observarInscriptos()
        observarProgressBar()

        viewModel.obtenerInscriptos(idCurso)

    }

    private fun observarProgressBar() {
        viewModel.obtenerShowProgressbarObservable().observe(this,{
            if(it) progressBar.visibility= View.VISIBLE
            else progressBar.visibility= View.GONE
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(VerInscriptosActivityViewModel::class.java)

        viewModel.usuarios.observe(this,{ usuarios ->
            if (!usuarios.isNullOrEmpty()){
                colaboradores = mutableListOf()
                colabUsernames.forEach { name ->
                   colaboradores.add( usuarios.first { user -> user.username == name })
                }

                inscriptos = mutableListOf()
                inscriptosUsernames.forEach { name ->
                    if (colabUsernames.contains(name) || name == ownerUsername) return@forEach
                    inscriptos.add( usuarios.first { user -> user.username == name })
                }

                owner = usuarios.first { user -> user.username == ownerUsername }

                inscriptosAdapter.colaboradores = colabUsernames.toMutableList()
                inscriptosAdapter.inscriptos = (colaboradores + inscriptos).toMutableList()
                inscriptosAdapter.inscriptos.add(0,owner)
                inscriptosAdapter.notifyDataSetChanged()
            }
        })

        viewModel.colaborador.observe(this,{
            Log.e("ok","ok")
        })
    }


    private fun observarInscriptos(){
        viewModel.obtenerInscriptosObservable().observe(this,{
            if(it == null || it.isEmpty()) {
                Toast.makeText(this@VerInscriptosActivity, "No hay alumnos inscriptos...", Toast.LENGTH_LONG).show()
            } else{
                inscriptosUsernames = it.toTypedArray()
                viewModel.obtenerUsuarios()
            }
        })
    }

    private fun initRecyclerView(owner : String, user:String){
        recyclerViewInscriptos.apply {
            layoutManager = LinearLayoutManager(this@VerInscriptosActivity)
            inscriptosAdapter = InscriptosRecyclerViewAdapter(this@VerInscriptosActivity,owner,user)
            adapter = inscriptosAdapter
        }}


    override fun onItemClick(inscripto: Usuario) {

        val intent = Intent(this@VerInscriptosActivity, PerfilActivity::class.java)
        // Le paso el email a PerfilActivity para que muestre el perfil de ese usuario.
        intent.putExtra("email", inscripto.username)

        startActivity(intent)
    }

    override fun eliminarColaborador(usuario: Usuario) {
        //viewModel.eliminarColaborador(usuario,idCurso)
    }

    override fun agregarColaborador(usuario: Usuario) {

        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setTitle("Agregar Colaborador")
        alert.setMessage("¿Está seguro que desea incluir a ${usuario.nombre?.capitalize()} ${usuario.apellido?.capitalize()}  a la lista de colaboradores?")
        alert.setPositiveButton("Sí") { dialog, which ->

            viewModel.agregarColaborador(usuario,idCurso)
            dialog.dismiss()
        }

        alert.setNegativeButton("No", { dialog, which -> dialog.dismiss() })

        alert.show()
    }

}