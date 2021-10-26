package com.ubademy_mobile.view_models.tools

import android.util.Log
import com.google.gson.Gson
import com.ubademy_mobile.services.data.Usuario
import retrofit2.Response
import java.util.*

fun logFailure(context: String, t:Throwable){
    Log.d(
        "${context} onFailure",
        "Localized message: ${t.localizedMessage!!}\n"+
                "Cause:             ${t.cause!!}"
    )
}

fun logResponse(context: String, r: Response<*>){
    Log.d("${context} onResponse",
        "Message:       ${Gson().toJson(r.message())}) \n" +
                "Successful: ${r.isSuccessful}\n" +
                "Body:          ${r.body()}")
}