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
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.ubademy_mobile.Fragments.ListadoCursosFragment
import com.ubademy_mobile.Fragments.GaleriaDeCursosFragment
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.tools.Themes
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Device
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_menu.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {

    private var galleryMode: Boolean = false
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

    override fun onRestart() {
        super.onRestart()

        finish()
        startActivity(intent)
    }

    private fun setup() {

        setSupportActionBar(main_toolbar)

        toogle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu, R.string.close_menu)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        setupMainMenu()

        initThemes()

        searchPreferences = SearchPreferences(this)
        initSearchBox()

        BtnFlecha.setOnClickListener {
            toggleSearchMode()
        }

        BtnIniciarBusqueda.setOnClickListener {
            viewCoursesOf(null)
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

    private fun initThemes() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        var transaction = supportFragmentManager.beginTransaction()

        Themes.values().forEach {
            val newView = FragmentContainerView(this@MainActivity)
            themesContainer.addView(newView)
            newView.id = View.generateViewId()
            val newFragment = ListadoCursosFragment.newInstance(email.toString(),it.toString())
                transaction.setReorderingAllowed(true)
                transaction.add(newView.id, newFragment,newFragment.tag).addToBackStack(it.name)
        }
        transaction.commit()
    }

    private fun goToMiPerfil(){
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
                R.id.suscribirse_menu -> startActivity(Intent(this@MainActivity, SuscripcionActivity::class.java))
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

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            val token = task.result
            // aca hay que llamar al back para registrar este device al usuario
            Log.d("DeviceID", token)

            val baseUrl = Constants.API_USUARIOS_URL
            val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
            val call = retroInstance.borrarDevice(token)

            call.enqueue(object: Callback<Device> {
                override fun onFailure(call: Call<Device>, t: Throwable){
                    Log.d("onFailure", t.localizedMessage)
                    logout()
                }

                override fun onResponse(call: Call<Device>, response: Response<Device>){
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("EXIT", true)
                    startActivity(intent)
                }
            })
        })
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
            FilterBox.visibility = View.VISIBLE
            searchMode = "PRO"

        }else {
            BtnFlecha.text = "▶"
            FilterBox.visibility = View.GONE
            searchMode = "BASIC"
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        searchPreferences.suscripcion = parent?.getItemAtPosition(position).toString().toLowerCase()
        viewModel.filtrarCursos(searchPreferences)

        Log.d("Busqueda filtrada", searchPreferences.toString())

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    fun viewCoursesOf(theme: Themes?) {

        galleryMode = true

        supportFragmentManager.popBackStack()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        val newView = FragmentContainerView(this@MainActivity)
        themesContainer.addView(newView)
        newView.id = View.generateViewId()
        val newFragment = GaleriaDeCursosFragment.newInstance(email.toString(), theme.toString())
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(newView.id, newFragment,newFragment.tag)
            .addToBackStack(theme.toString())
            .commit()


    }

    override fun onBackPressed() {

        if (galleryMode){

            supportFragmentManager.popBackStack()
            initThemes()
            galleryMode=false
        }else{
            super.onBackPressed()
        }
    }


}