package com.ubademy_mobile.services.data

import android.provider.ContactsContract

data class Usuario(val id: Int?,
                   val email: ContactsContract.CommonDataKinds.Email?,
                   val nombre: String?,
                   val apellido: String?,
                   val password: String?,
                   val ubicacion: String,
                   val preferencias: Array<String> )
