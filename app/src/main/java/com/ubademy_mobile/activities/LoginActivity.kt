package com.ubademy_mobile.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.*
import com.ubademy_mobile.view_models.LoginActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.utils.Constants
import com.ubademy_mobile.utils.ProviderType
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val GOOGLE_SING_IN = 100

    private val viewModel = LoginActivityViewModel()
    private var okMessage : Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Login")
        analytics.logEvent("InitScreen", bundle)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {task ->
            val token = task.result
            // aca hay que llamar al back para registrar este device al usuario

            //Log.d("DeviceID", token)
        })


        setup()
        checkSession()

        observarStatusBar()
        observarCredenciales()
    }

    override fun onStart() {
        super.onStart()

        loginLayout.visibility = View.VISIBLE
        checkSession()
    }

    private fun checkSession() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        Log.e("check Session", prefs.getString("access_token", null).toString())

        Constants.TOKEN = prefs.getString("access_token", null)

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
                if(it == null){
                    clearSession()
                    showAlert();
                } else{
                    it.access_token?.run{
                        okMessage!!.show()
                        showHome(TxtEmail.editText!!.text.toString(), ProviderType.BASIC)
                        clearTextFields()
                    }
                }
            })
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.toString())
        }
        startActivity(homeIntent)
    }

    private fun setup() {

        BtnLogin.setOnClickListener {
            if(validarEmail() and validarContrasena()){
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
            username = TxtEmail.editText!!.text.toString(),
            password = TxtPassword.editText!!.text.toString()
        )

        // Llamada al back
        viewModel.loginUsuario(credenciales)

        viewModel.getTokenObservable().observe(this,
            {
                if(it == null){
                    Log.e("token al login", "error, es null")
                    clearSession()
                    showAlert();
                } else{
                    it.access_token?.run{
                        /*val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("access_token", it.access_token.toString())
                        prefs.apply()
                        val prefs2 = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                        Log.e("token al login", "access token " + prefs2.getString("access_token", null).toString())*/
                        Constants.TOKEN = it.access_token

                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("access_token", Constants.TOKEN)
                        prefs.apply()

                        okMessage!!.show()
                        showHome(TxtEmail.editText!!.text.toString(), ProviderType.BASIC)
                        clearTextFields()
                        initSession(credenciales.username, ProviderType.BASIC.toString())
                    }
                }
            })
    }

    fun loginWithGoogle(view: android.view.View) {

        val googleConf =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent,GOOGLE_SING_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( requestCode == GOOGLE_SING_IN){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)
                Log.d("Login with Google",account.toString())

                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if (it.isSuccessful) {
                            var token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                                ?.addOnCompleteListener {

                                    if (it.isSuccessful()) {
                                        var idToken = it.getResult().token

                                        val ubademyToken = UbademyToken(
                                            firebase_token = idToken
                                        )

                                        // Send token to your backend via HTTPS
                                        viewModel.swap(ubademyToken)

                                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

                                        viewModel.getTokenObservable().observe(this,
                                            { res ->
                                                if(res == null){
                                                    clearSession()
                                                    showAlert();
                                                } else{
                                                    res.access_token?.run{
                                                        Log.e("token al login", res.access_token)
                                                        prefs.putString("access_token", res.access_token)
                                                        prefs.apply()
                                                        Constants.TOKEN = res.access_token
                                                        okMessage!!.show()
                                                        showHome(TxtEmail.editText!!.text.toString(),
                                                            ProviderType.BASIC)
                                                        clearTextFields()
                                                    }
                                                }
                                            })

                                        initSession(account.email!!.toString(), ProviderType.GOOGLE.toString())

                                    } else {
                                        // Handle error -> task.getException();
                                    }

                                }

                            Log.d("firebase_token => ", token.toString())

                        }else{
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                Log.e("Login with Google",e.toString())
                showAlert()
            }

        }
    }

    fun validarContrasena(): Boolean {

        var result = true
        if ( TxtPassword.editText?.text?.length == 0) {
            TxtPassword.error = "Ingresa una contraseña"
            TxtPassword.isErrorEnabled = true
            result = false
        }else{
            TxtPassword.error = null
            TxtPassword.isErrorEnabled = false
        }

        return result
    }

    fun validarEmail(): Boolean {

        if ( TxtEmail.editText?.text?.length == 0 ) {
            TxtEmail.error = "El email no puede estar vacío"
            TxtEmail.isErrorEnabled = true
            return false
        }else{
            TxtEmail.error = null
            TxtEmail.isErrorEnabled = false
        }

        val regex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
        if (!regex.containsMatchIn(TxtEmail.editText!!.text)) {
            TxtEmail.error = "Ingresa un correo válido"
            TxtEmail.isErrorEnabled = true
            return false
        }else{
            TxtEmail.error = null
            TxtEmail.isErrorEnabled = false
        }

        return true

    }

    fun initSession(email : String, provider: String){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider",provider)
        prefs.apply()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {task ->
            val token = task.result
            // aca hay que llamar al back para registrar este device al usuario
            Log.d("DeviceID", token)

            val baseUrl = Constants.API_USUARIOS_URL
            val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
            val call = retroInstance.registrarDevice(Device(username = email, device = token))

            call.enqueue(object: Callback<Device> {
                override fun onFailure(call: Call<Device>, t: Throwable){
                    Log.d("onFailure", t.localizedMessage)
                    clearSession()
                    initSession(email, provider)
                }

                override fun onResponse(call: Call<Device>, response: Response<Device>){

                }
            })

            val callUser = retroInstance.obtenerUsuario(email)

            callUser.enqueue(object: Callback<UsuarioResponse> {
                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable){
                    Log.d("onFailure", t.localizedMessage)
                    clearSession()
                    initSession(email, provider)
                }

                override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>){
                    prefs.putString("full_name", response.body()!!.data!!.nombre + " " + response.body()!!.data!!.apellido)
                    prefs.apply()
                //Log.d("apellido", "---------------- " + prefs)
                }
            })

        })


    }

    private fun clearSession() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
    }

    fun clearTextFields(){

        TxtEmail.clearFocus()
        TxtPassword.clearFocus()
        TxtEmail.editText!!.text.clear()
        TxtPassword.editText!!.text.clear()
    }
}