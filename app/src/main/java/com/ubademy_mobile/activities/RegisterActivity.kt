package com.ubademy_mobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ubademy_mobile.R
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.view_models.RegisterActivityViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val viewModel = RegisterActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        BtnFinalizarRegistro.setOnClickListener {
            registrarUsuario()
        }

        BtnVolver.setOnClickListener {
            finish()
        }

        observarStatusBar()

    }

    private fun observarStatusBar() {
        viewModel.getStatusBarObservable().observe(this,{
            if(it) progressBar.visibility= View.VISIBLE
            else progressBar.visibility=View.GONE
        })
    }

    private fun registrarUsuario() {

        if (!validarEmail() or !validarNombres() or !validarContrasenas()) return

        val nuevoUsuario = Usuario(
            username = txtCorreo.editText!!.text.toString(),
            nombre = txtNombre.editText!!.text.toString(),
            apellido = txtApellido.editText!!.text.toString(),
            password = txtContrasena.editText!!.text.toString())

        viewModel.registrarUsuario(nuevoUsuario)

        Toast.makeText(this,"REGISTRACION FINISHED",Toast.LENGTH_LONG).show()

    }

    private fun validarEmail(): Boolean {

        if ( txtCorreo.editText?.text?.length == 0 ) {
            txtCorreo.error = "El email no puede estar vacío"
            txtCorreo.isErrorEnabled = true
            return false
        }else{
            txtCorreo.error = null
            txtCorreo.isErrorEnabled = false
        }

        val regex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
        if (!regex.containsMatchIn(txtCorreo.editText!!.text)) {
            txtCorreo.error = "Ingresa un correo válido"
            txtCorreo.isErrorEnabled = true
            return false
        }else{
            txtCorreo.error = null
            txtCorreo.isErrorEnabled = false
        }

        return true

    }

    private fun validarNombres(): Boolean {

        var result = true
        if ( txtNombre.editText?.text?.length == 0 ) {
            txtNombre.error = "Ingresa al menos un nombre"
            txtNombre.isErrorEnabled = true
            result = false
        }else{
            txtNombre.error = null
            txtNombre.isErrorEnabled = false
        }

        if ( txtApellido.editText?.text?.length == 0 ) {
            txtApellido.error = "Ingresa al menos un apellido"
            txtApellido.isErrorEnabled = true
            result = false
        }else{
            txtApellido.error = null
            txtApellido.isErrorEnabled = false
        }

        return result

    }

    private fun validarContrasenas(): Boolean {

        var result = true
        if ( txtContrasena.editText?.text?.length == 0) {
            txtContrasena.error = "Ingresa una contraseña"
            txtContrasena.isErrorEnabled = true
            result = false
        }else{
            txtContrasena.error = null
            txtContrasena.isErrorEnabled = false
        }

        if (txtRepetirContrasena.editText!!.text!!.toString() != txtContrasena.editText!!.text.toString() ) {
            txtRepetirContrasena.error = "Las contraseñas no coinciden"
            txtRepetirContrasena.isErrorEnabled = true
            result = false
        }else{
            txtRepetirContrasena.error = null
            txtRepetirContrasena.isErrorEnabled = false
        }

        return result
    }

}