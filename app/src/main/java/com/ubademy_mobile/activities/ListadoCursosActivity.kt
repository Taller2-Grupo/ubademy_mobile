package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.ubademy_mobile.R
import com.ubademy_mobile.services.Curso
import com.ubademy_mobile.services.RecyclerViewAdapter
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.Device
import com.ubademy_mobile.services.data.UsuarioResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.utils.SearchPreferences
import com.ubademy_mobile.view_models.ListadoCursosActivityViewModel
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import kotlinx.android.synthetic.main.activity_listado_cursos.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.bottom_menu_navigation.*
import kotlinx.android.synthetic.main.header_menu.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListadoCursosActivity:
    AppCompatActivity(),
    RecyclerViewAdapter.OnItemClickListener,
    AdapterView.OnItemSelectedListener {

    private var searchMode = "BASIC"
    lateinit var searchPreferences : SearchPreferences

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

        // TODO: Sacar el nombre del jwt cuando se agregue.
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email != null){
            val retroInstance = RetroInstance.getRetroInstance(Constants.API_USUARIOS_URL)
                .create(UsuarioService::class.java)
            val call = retroInstance.obtenerUsuario(email!!)

            call.enqueue(object : Callback<UsuarioResponse> {
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    logFailure("ListadoCursos", t)
                }

                override fun onResponse(
                    call: Call<UsuarioResponse>,
                    response: Response<UsuarioResponse>
                ) {

                    logResponse("PerfilUsuario", response)

                    if (response.isSuccessful) {
                        val headerLayout = navigation_menu.getHeaderView(0) // 0-index header
                        headerLayout.name_user_menu.text = response.body()?.data!!.nombre
                    }
                }
            })
        }
    }

    override fun onRestart() {
        super.onRestart()

        viewModel.getCursos()
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
        val intent = Intent(this@ListadoCursosActivity, PerfilActivity::class.java)
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
                R.id.crear_curso_menu -> startActivity(Intent(this@ListadoCursosActivity, CrearCursoActivity::class.java))
                R.id.chat_menu -> startActivity(Intent(this@ListadoCursosActivity, Chat2Activity::class.java))
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

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        val intent = Intent(this@ListadoCursosActivity, VerCursoActivity::class.java)
        intent.putExtra("curso_id", curso.id)
        intent.putExtra("descripcion", curso.descripcion)
        intent.putExtra("titulo", curso.titulo)
        intent.putExtra("usuario", email)
        //usuarios
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000){
            viewModel.getCursos()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun logout(){
        val prefsEditor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefsEditor.clear()
        prefsEditor.apply()

        FirebaseAuth.getInstance().signOut()
        onBackPressed()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {task ->
            val token = task.result
            // aca hay que llamar al back para registrar este device al usuario
            Log.d("DeviceID", token)

            val baseUrl = "https://ubademy-usuarios.herokuapp.com/"
            val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
            val call = retroInstance.borrarDevice(token)

            call.enqueue(object: Callback<Device> {
                override fun onFailure(call: Call<Device>, t: Throwable){
                    Log.d("onFailure", t.localizedMessage)
                    logout()
                }

                override fun onResponse(call: Call<Device>, response: Response<Device>){

                }
            })
        })

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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        searchPreferences.suscripcion = parent?.getItemAtPosition(position).toString().lowercase()
        viewModel.filtrarCursos(searchPreferences)

        Log.d("Busqueda filtrada", searchPreferences.toString())

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}