package com.ubademy_mobile.Fragments

import androidx.recyclerview.widget.RecyclerView
import com.ubademy_mobile.Adapter.UserAdapter
import com.ubademy_mobile.services.data.Usuario
import com.google.firebase.database.DatabaseReference
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.ubademy_mobile.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.ubademy_mobile.services.data.Mensaje
import com.google.firebase.database.DatabaseError
import com.ubademy_mobile.services.RetroInstance
import com.ubademy_mobile.services.data.GetUsersResponse
import com.ubademy_mobile.services.interfaces.UsuarioService
import com.ubademy_mobile.view_models.tools.logResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatsFragment(var userid: String) : Fragment() {
    lateinit var rView: RecyclerView
    lateinit var adapter: UserAdapter
    private val mUsuarios: MutableList<Usuario> = ArrayList()
    var ref: DatabaseReference? = null
    private val userList: MutableList<String> = ArrayList()
    private val usuariosRenderizados: MutableList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        rView = view.findViewById(R.id.chats)
        rView.setHasFixedSize(true)
        rView.layoutManager = LinearLayoutManager(context)
        ref = FirebaseDatabase.getInstance().getReference("Mensajes")

        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (snap in snapshot.children) {
                    val msj = snap.getValue(Mensaje::class.java)
                    Log.d("msj", msj.toString())
                    if (msj!!.emisor == userid) userList.add(msj.receptor)
                    if (msj.receptor == userid) userList.add(msj.emisor)
                }
                renderChats()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return view
    }

    private fun renderChats() {
        val baseUrl = "https://ubademy-usuarios.herokuapp.com/"
        val retroInstance = RetroInstance.getRetroInstance(baseUrl).create(UsuarioService::class.java)
        val call = retroInstance.obtenerUsuarios()

        call.enqueue(object: Callback<GetUsersResponse> {
            override fun onFailure(call: Call<GetUsersResponse>, t: Throwable){
                Log.d("onFailure", t.localizedMessage)
            }

            override fun onResponse(call: Call<GetUsersResponse>, response: Response<GetUsersResponse>){
                //logResponse("obtenerInscriptos", response)
                mUsuarios.clear()
                usuariosRenderizados.clear()
                if(response.isSuccessful){
                    response.body()?.data?.forEach {
                        if (it.username !== null && userList.contains(it.username) && !usuariosRenderizados.contains(it.username)) {
                            Log.d("onresponse", it.username)
                            mUsuarios.add(it)
                            usuariosRenderizados.add(it.username)
                        }
                    }
                    adapter = UserAdapter(context, mUsuarios, true, userid)
                    rView.adapter = adapter
                }
            }
        })
    }
}