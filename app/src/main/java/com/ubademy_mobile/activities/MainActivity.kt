package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ubademy_mobile.Fragments.ListadoCursosFragment
import com.ubademy_mobile.R
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_menu.view.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {

    lateinit var viewModel: MainActivityViewModel
    lateinit var toogle: ActionBarDrawerToggle

    private var searchMode = "BASIC"
    lateinit var searchPreferences : SearchPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        setup()

        // TODO: Sacar el nombre del jwt cuando se agregue.
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email != null){
            viewModel.getLoggedUser(email)
        }

        val fragment = ListadoCursosFragment.newInstance(email.toString())
        val manager = supportFragmentManager
        manager.beginTransaction().replace(R.id.fragmentContainerView,fragment,fragment.tag).commit()
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.loggedUser.observe(this, {
            if(it != null) {

                val headerLayout = navigation_menu.getHeaderView(0) // 0-index header
                headerLayout.name_user_menu.text = it.nombre
            }
        })

    }

    private fun setup() {

        setSupportActionBar(main_toolbar)

        toogle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu, R.string.close_menu)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        setupMainMenu()


        searchPreferences = SearchPreferences(this)
        initSearchBox()

        BtnFlecha.setOnClickListener {
            toggleSearchMode()
        }

        EditTxTBuscarCurso.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                searchPreferences.patron = s.toString()
                viewModel.filtrarCursos(searchPreferences)
            }
        })

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.suscripcion,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnCategorias.adapter = adapter
            spnCategorias.onItemSelectedListener = this
        }

    }

    fun goToMiPerfil(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val intent = Intent(this@MainActivity, PerfilActivity::class.java)
        // Le paso el email a PerfilActivity para que muestre el perfil de ese usuario.
        intent.putExtra("email", email)

        startActivity(intent)
    }

    private fun setupMainMenu(){
        navigation_menu.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.perfil_menu -> goToMiPerfil()
                R.id.home_menu -> Toast.makeText(applicationContext, "Home Clickeado", Toast.LENGTH_LONG).show()
                R.id.mis_cursos_menu -> Toast.makeText(applicationContext, "Mis Cursos Clickeado", Toast.LENGTH_LONG).show()
                R.id.crear_curso_menu -> startActivity(Intent(this@MainActivity, CrearCursoActivity::class.java))
                R.id.chat_menu -> startActivity(Intent(this@MainActivity, Chat2Activity::class.java))
                R.id.logout_menu -> logout()
            }
            true
        }
    }

    fun logout(){
        val prefsEditor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefsEditor.clear()
        prefsEditor.apply()

        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun initSearchBox(){
        checkBoxIdioma.isChecked = true
        checkBoxProgramacion.isChecked = true
        checkBoxMultimedia.isChecked = true

        checkBoxIdioma.setOnClickListener{
            if(it is CheckBox)
                if (it.isChecked) searchPreferences.categorias.add(getString(R.string.categoria_idioma))
                else  searchPreferences.categorias.remove(getString(R.string.categoria_idioma))
            viewModel.filtrarCursos(searchPreferences)
        }

        checkBoxProgramacion.setOnClickListener{
            if(it is CheckBox)
                if (it.isChecked) searchPreferences.categorias.add(getString(R.string.categoria_programacion))
                else  searchPreferences.categorias.remove(getString(R.string.categoria_programacion))
            viewModel.filtrarCursos(searchPreferences)
        }

        checkBoxMultimedia.setOnClickListener{
            if(it is CheckBox)
                if (it.isChecked) searchPreferences.categorias.add(getString(R.string.categoria_multimedia))
                else  searchPreferences.categorias.remove(getString(R.string.categoria_multimedia))
            viewModel.filtrarCursos(searchPreferences)
        }
    }

    fun toggleSearchMode(){

        if(searchMode == "BASIC"){

            BtnFlecha.text = "▼"
            SearchBox.visibility = View.VISIBLE
            searchMode = "PRO"

        }else {

            BtnFlecha.text = "▶"
            SearchBox.visibility = View.GONE
            searchMode = "BASIC"
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        searchPreferences.suscripcion = parent?.getItemAtPosition(position).toString().lowercase()
        viewModel.filtrarCursos(searchPreferences)

        Log.d("Busqueda filtrada", searchPreferences.toString())

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}