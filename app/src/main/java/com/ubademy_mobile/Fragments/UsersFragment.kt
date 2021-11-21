package com.ubademy_mobile.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.Adapter.UserAdapter
import com.ubademy_mobile.R
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.GetUsersResponse
import com.ubademy_mobile.services.data.Usuario
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.view_models.tools.logFailure
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class UsersFragment(var userid: String) : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var usuarios: MutableList<Usuario>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(context)

        usuarios = ArrayList()

        getUsuarios()

        return view
    }

    fun getUsuarios(){
        val baseUrl = "https://ubademy-usuarios.herokuapp.com/"
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
        val call = retroInstance.obtenerUsuarios()
        call.enqueue(object: Callback<GetUsersResponse> {
            override fun onFailure(call: Call<GetUsersResponse>, t: Throwable){
                Log.d("onFailure", t.localizedMessage)
                //logFailure("obtenerUsuarios" , t)
            }

            override fun onResponse(call: Call<GetUsersResponse>, response: Response<GetUsersResponse>){
                logResponse("obtenerInscriptos", response)
                if(response.isSuccessful){
                    response.body()?.data?.forEach {
                        usuarios.add(it)
                    }
                    userAdapter = UserAdapter(context, usuarios, false, userid)
                    recyclerView.adapter = userAdapter
                }
            }
        })
    }
}