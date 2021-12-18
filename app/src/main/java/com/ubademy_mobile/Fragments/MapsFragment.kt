package com.ubademy_mobile.Fragments

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ubademy_mobile.R
import com.ubademy_mobile.activities.OnDataPass

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val latitud = arguments?.getDouble("latitud") ?: 0.0
        val longitud = arguments?.getDouble("longitud") ?: 0.0

        Log.e("LOG", "A ver $latitud $longitud")

        val ubicacionUsuario = LatLng(latitud, longitud)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))

        if (arguments?.getDouble("latitud") != null) {
            googleMap.addMarker(MarkerOptions().position(ubicacionUsuario).title("Mi ubicación"))
        }

        googleMap.setOnMapClickListener { param ->
            googleMap.clear()
            Log.e("On map click", param.toString())
            googleMap.addMarker(MarkerOptions().position(param).title("Mi ubicación"))
            dataPasser.onDataPass(param)
        }
    }

    lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}