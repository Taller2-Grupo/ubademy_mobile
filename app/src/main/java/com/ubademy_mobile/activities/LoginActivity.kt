package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.LoginActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*
import com.ubademy_mobile.services.data.Credenciales

class LoginActivity : AppCompatActivity() {

    private val viewModel = LoginActivityViewModel()
    private var okMessage : Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Login")
        analytics.logEvent("InitScreen", bundle)

        setup()
        session()

        observarStatusBar()
        observarCredenciales()
    }

    override fun onStart() {
        super.onStart()

        loginLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        // quiere decir que ya hay una session iniciada
        if(email != null && provider != null){
            loginLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun observarStatusBar() {
        viewModel.getStatusBarObservable().observe(this,{
            if(it) LoginProgressBar.visibility= View.VISIBLE
            else LoginProgressBar.visibility=View.GONE
        })
    }

    private fun observarCredenciales(){

        okMessage = Toast.makeText(this,"Logueado correctamente", Toast.LENGTH_LONG)

        viewModel.getTokenObservable().observe(this,
            {
                it?.access_token?.run{
                    okMessage!!.show()

                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.putString("email", TxtEmail.text.toString())
                    prefs.putString("provider", ProviderType.BASIC.toString())
                    prefs.apply()

                    showHome(TxtEmail.text.toString(),ProviderType.BASIC)
                }
            })
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.toString())
        }
        startActivity(homeIntent)
    }

    private fun setup() {
        BtnLogin.setOnClickListener {
            if(TxtEmail.text.isNotEmpty() && TxtPassword.text.isNotEmpty()){
                loginWithCredentials()
            }
        }

        BtnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        BtnGoogleLogin.setOnClickListener {
            loginWithGoogle(it)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error al intentar loguearse")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun loginWithCredentials(){

        val credenciales = Credenciales(
            grant_type = "",
            username = TxtEmail.text.toString(),
            password = TxtPassword.text.toString(),
            scope = "",
            client_id = "",
            client_secret = ""
        )

        viewModel.loginUsuario(credenciales)
    }

    fun loginWithGoogle(view: android.view.View) {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.prefs_file)).requestEmail().build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
    }
}


/*
* FirebaseAuth.getInstance().signInWithEmailAndPassword(TxtEmail.text.toString(), TxtPassword.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else{
                        showAlert()
                    }
                }
* */