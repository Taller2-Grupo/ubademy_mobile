package com.ubademy_mobile.utils
import android.content.Context
import android.util.Log
import com.ubademy_mobile.R
import java.text.Normalizer


class SearchPreferences(context: Context) {

    var patron = ""

    var categorias = mutableSetOf(
        context.getString(R.string.categoria_idioma),
        context.getString(R.string.categoria_programacion),
        context.getString(R.string.categoria_multimedia))

    var suscripcion = "Todas"

    override fun toString(): String {

        return "Patron ${patron} \n" +
                "categorias ${categorias}\n" +
                "suscripcion ${suscripcion}"
    }


    fun isPatternIn(titulo : String): Boolean {

        var result = true
        var titulo_norm = stringNormalizer(titulo).lowercase()
        val patterns = stringNormalizer(patron).lowercase().split(" ")

        patterns.forEach{
            if(!titulo_norm.contains(it)) result = false
        }

        return result
    }

    private fun stringNormalizer(string: String) : String {

        return Normalizer.normalize(string, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
    }

    fun isSuscription(suscripcion_target : String): Boolean {

        Log.d("isSuscription","suscripcion: ${suscripcion}")
        if(suscripcion == "todos") {
            return true
        }

        return suscripcion == suscripcion_target
    }

    fun isCategory(categoria_target : String): Boolean {

        return categorias.contains(categoria_target)
    }

}
