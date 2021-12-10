package com.ubademy_mobile.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import com.ubademy_mobile.R
import com.ubademy_mobile.view_models.VerExamenesActivityViewModel
import kotlinx.android.synthetic.main.fragment_examen.*
import kotlinx.android.synthetic.main.fragment_examen.view.*

class ExamenFragment : Fragment() {

    val viewModel: VerExamenesActivityViewModel by activityViewModels()
    private lateinit var appActivity: FragmentActivity
    //private lateinit var viewModel: VerExamenesActivityViewModel
    private lateinit var appContext: Context
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_examen, container, false)
        view.TxTConsigna.setOnClickListener {
            Log.e("listener","CLick in examen Listener")
            Navigation.findNavController(view).navigate(R.id.navigateToRespuestas)
        }
        appActivity = requireActivity()
        appContext = requireContext()
        return view
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            ExamenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {

        //viewModel = ViewModelProvider(context as ViewModelStoreOwner).get(VerExamenesActivityViewModel::class.java)
        viewModel.examen_seleccionado.value.apply {
            Log.e("examen seleccionado obs","${this?.consignas?.get(0)?.enunciado}")
            TxTConsigna.text = this?.consignas?.get(0)?.enunciado.toString()
        }
    }
}