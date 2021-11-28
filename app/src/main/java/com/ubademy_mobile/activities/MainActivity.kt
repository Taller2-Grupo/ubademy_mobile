package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ubademy_mobile.Fragments.ListadoCursosFragment
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_menu.view.*


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel
    lateinit var toogle: ActionBarDrawerToggle

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

}