package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.view_models.ListadoCursosActivityViewModel
import kotlinx.android.synthetic.main.activity_listado_cursos.*

class ListadoCursosActivity: AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {

    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: ListadoCursosActivityViewModel

    lateinit var toogle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_cursos)

        initRecyclerView()

        initViewModel()
        viewModel.getCursos()

        setup()
    }

    private fun setup() {
        setSupportActionBar(main_toolbar)

        toogle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu, R.string.close_menu)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        navigation_menu.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.perfil_menu -> Toast.makeText(applicationContext, "Perfil Cliqueado", Toast.LENGTH_LONG).show()
                R.id.home_menu -> Toast.makeText(applicationContext, "Home Cliqueado", Toast.LENGTH_LONG).show()
                R.id.mis_cursos_menu -> Toast.makeText(applicationContext, "Mis Cursos Cliqueado", Toast.LENGTH_LONG).show()
                R.id.crear_curso_menu -> startActivity(Intent(this@ListadoCursosActivity, CrearCursoActivity::class.java))
                R.id.logout_menu -> logout()
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView(){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListadoCursosActivity)
            val decoration = DividerItemDecoration(this@ListadoCursosActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = RecyclerViewAdapter(this@ListadoCursosActivity)
            adapter = recyclerViewAdapter
        }
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(this).get(ListadoCursosActivityViewModel::class.java)
        viewModel.getCursosObservable().observe(this, Observer<List<Curso>>{
            if(it == null){
                Toast.makeText(this@ListadoCursosActivity, "No hay datos...", Toast.LENGTH_LONG).show()
            } else{
                recyclerViewAdapter.cursos = it.toMutableList()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onItemEditClick(curso: Curso) {
        val intent = Intent(this@ListadoCursosActivity, CrearCursoActivity::class.java)
        intent.putExtra("curso_id", curso.id)
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000){
            viewModel.getCursos()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun logout(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }
}