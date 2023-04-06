package com.scouto.assignment.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scouto.assignment.MainActivity
import com.scouto.assignment.R
import com.scouto.assignment.adapter.MakesAdapter
import com.scouto.assignment.adapter.ModelAdapter
import com.scouto.assignment.adapter.YourCarAdapter
import com.scouto.assignment.data.model.YourCar
import com.scouto.assignment.data.model.makes.AllMakesResult
import com.scouto.assignment.data.model.singlemake.SingleMake
import com.scouto.assignment.databinding.FragmentDetailBinding
import com.scouto.assignment.mvvm.viewModel.AppViewModel
import com.scouto.assignment.utils.SharedPreferencesTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var binding: FragmentDetailBinding? = null
    private var p1:String? = null
    private var p2:String? = null
    private lateinit var adapter : YourCarAdapter
    private var makeDialogFlag = false
    private var modelDialogFlag = false

    private val sharedPreferences by lazy {
        activity?.getSharedPreferences(SharedPreferencesTag.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private val viewModel: AppViewModel by viewModels()

    private lateinit var register : ActivityResultLauncher<String?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(layoutInflater,container,false)
        register = registerForActivityResult(ActivityResultContracts.GetContent()){ uri : Uri?->
            val dir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri!!)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            viewModel.uploadImage(uri, dir, bitmap)
        }
        return binding!!.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DetailFragment().apply {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainUI()

        initClickListener()

    }

    private fun mainUI(){
        // <----------------------  Show Loading when fetching data from api------------------------>
        viewModel.status.observe(viewLifecycleOwner) {status ->
            when(status) {
                null -> {
                    binding?.progressBar?.visibility = View.GONE
                }
                true -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                false -> {
                    binding?.progressBar?.visibility = View.GONE
                }
            }
        }
        // <--------------------- Fetching data when click on refresh button ----------------->
        binding?.refreshBtn?.setOnClickListener {
            viewModel.allOfflineMakes.removeObservers(viewLifecycleOwner)
            CoroutineScope(Dispatchers.IO).launch {
                if(viewModel.deleteOldData()){
                    viewModel.getAllMake()
                }
            }
        }

        // <--------------------- Fetching data when click on select make edit text ----------------->
        binding?.selectMakeLayout?.setOnClickListener {
            binding?.makeErrorText?.visibility = View.GONE
            // <--------------- observing allmakes from viewmodel after fetch from api ----------------->
            viewModel.allOfflineMakes.observe(viewLifecycleOwner) {
                if (it != null && it.isNotEmpty()){
                    if (!makeDialogFlag) showMakeDialog(it)
                } else {
                    viewModel.getAllMake()
                }
            }
        }

        // <--------------------- Fetching data when click on select model edit text ----------------->
        binding?.selectModelLayout?.setOnClickListener {
            if( viewModel.selectedMake.Make_Name.isEmpty()){
                binding?.makeErrorText?.visibility = View.VISIBLE
            } else {
                binding?.modelErrorText?.visibility = View.GONE
                viewModel.getModel()
            }
        }

        // <--------------- observing single make from viewModel after fetch from api ----------------->
        viewModel.singleModel.observe(viewLifecycleOwner) {
            it?.let {
                showModelDialog(it)
            }
        }

        // Handling Room Database and adding or removing cars from database
        handleRoom()
    }

    private  fun initClickListener(){

        binding?.logOutBtn?.setOnClickListener {
            binding?.mainLayout?.visibility = View.GONE
            binding?.progressBar?.visibility = View.VISIBLE
            sharedPreferences?.edit()?.apply {
                putBoolean(SharedPreferencesTag.IS_LOGGED_IN,false)
                putLong(SharedPreferencesTag.LOGGED_USER_ID, -1)
                apply()
            }
            viewModel.userId = null
            viewModel.user = null
            viewModel.checkUserStatus?.value = null
            viewModel.addUserStatus?.value = null
            binding?.mainLayout?.visibility = View.VISIBLE
            binding?.progressBar?.visibility = View.GONE

            (requireActivity() as MainActivity).loadFragment(LoginSignUpFragment.newInstance())

        }
    }


    // <-----------------------------  Select Make Dialog --------------------------->
    private fun showMakeDialog(makes: List<AllMakesResult>) {
        makeDialogFlag = true
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setOnDismissListener {
            makeDialogFlag = false
        }

        val rv = dialog.findViewById<RecyclerView>(R.id.dialog_recycler_view)
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val makesAdapter = MakesAdapter(object : MakesAdapter.onItemClick {
            override fun onClick(position: Int) {
                viewModel.selectedMake = makes[position]
                binding?.selectedMakeTextView?.text = viewModel.selectedMake.Make_Name
                dialog.dismiss()
            }
        })

        rv.adapter = makesAdapter
        makesAdapter.datasetChanged(makes)

        if (makeDialogFlag)
            dialog.show()
    }

    // <---------------------------- Select Model --------------------------------->
    private fun showModelDialog(singleMake: SingleMake) {
        modelDialogFlag = true
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setOnDismissListener {
            modelDialogFlag = false
        }

        val rv = dialog.findViewById<RecyclerView>(R.id.dialog_recycler_view)
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val makesAdapter = ModelAdapter(object : ModelAdapter.onItemClick {
            override fun onClick(position: Int) {
                viewModel.selectedModel = singleMake.Results[position]
                binding?.selectedModelTextView?.text = viewModel.selectedModel.Model_Name
                dialog.dismiss()
            }
        })

        rv.adapter = makesAdapter
        makesAdapter.datasetChanged(singleMake.Results)

        if (modelDialogFlag)
            dialog.show()
    }

    // <----------------- Handling Room Operations ------------------------------->
    private fun handleRoom() {
        binding?.carsRecyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = YourCarAdapter(
            requireContext(),
            onDelete = object : YourCarAdapter.OnDelete{
                override fun onClick(position: Int, data: List<YourCar>) {
                    viewModel.deleteCar(data[position])
                }
            },
            onImage = object : YourCarAdapter.OnImage{
                override fun onClick(position: Int, data: List<YourCar>) {
                    viewModel.selectedCar = data[position]
                    register.launch("image/*")
                }
            }
        )

        binding?.carsRecyclerView?.adapter = adapter
        viewModel.allCars.observe(viewLifecycleOwner) {
            if (it != null) {
                if(it.isEmpty()){
                    binding?.emptyLayout?.visibility = View.VISIBLE
                } else {
                    binding?.emptyLayout?.visibility = View.GONE
                }
                adapter.datasetChange(it)
            } else {
                binding?.emptyLayout?.visibility = View.VISIBLE
            }
        }

        binding?.addCarBtn?.setOnClickListener {
            if (viewModel.selectedMake.Make_Name.isEmpty()){
                binding?.makeErrorText?.visibility = View.VISIBLE
            } else if(viewModel.selectedModel.Model_Name.isEmpty()) {
                binding?.modelErrorText?.visibility = View.VISIBLE
            } else {
                binding?.selectedMakeTextView?.text = getString(R.string.select_make)
                binding?.selectedModelTextView?.text = getString(R.string.select_model)
                viewModel.addCar(
                    YourCar(
                        make = viewModel.selectedMake.Make_Name,
                        model = viewModel.selectedModel.Model_Name,
                        makeId = viewModel.selectedMake.Make_ID,
                        modelId = viewModel.selectedModel.Model_ID
                    )
                )
            }

        }


    }
}