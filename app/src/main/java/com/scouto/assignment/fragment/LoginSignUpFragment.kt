package com.scouto.assignment.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.scouto.assignment.MainActivity
import com.scouto.assignment.R
import com.scouto.assignment.data.model.User
import com.scouto.assignment.databinding.FragmentSignUpLogInBinding
import com.scouto.assignment.mvvm.viewModel.AppViewModel
import com.scouto.assignment.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginSignUpFragment : Fragment() {

    private var binding:FragmentSignUpLogInBinding? = null
    private val sharedPreferences by lazy {
        activity?.getSharedPreferences(SharedPreferencesTag.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpLogInBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginSignUpFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initClickListener()
        initObserver()
    }

    private fun init(){

        setTextChangeListenerOnThisEditTextAndRemoveErrorWhenTextChanged(
            binding?.userEmailInputLayout,
            binding?.userEmailEditText
        )

        setTextChangeListenerOnThisEditTextAndRemoveErrorWhenTextChanged(
            binding?.userPasswordInputLayout,
            binding?.userPasswordEditText
        )

        if(!viewModel.loginSelected){
            setTextChangeListenerOnThisEditTextAndRemoveErrorWhenTextChanged(
                binding?.userNameInputLayout,
                binding?.userNameEditText
            )
        }
    }

    private fun initClickListener() {
        binding?.layoutChangeText?.setOnClickListener {


            //switch signup and Login Layout
            if(viewModel.loginSelected){
                binding?.apply {
                    viewModel.loginSelected = false
                    userNameInputLayout.visibility = View.VISIBLE
                    loginSignupBtn.text = getString(R.string.signup)
                    layoutChangePrefixText.text = getString(R.string.alaready_have_account)
                    layoutChangeText.text = getString(R.string.login)
                }
            }
            else{
                binding?.apply {
                    viewModel.loginSelected = true
                    userNameInputLayout.visibility = View.GONE
                    loginSignupBtn.text = getString(R.string.Login)
                    layoutChangePrefixText.text = getString(R.string.don_t_have_an_account)
                    layoutChangeText.text = getString(R.string.sign_up)
                }
            }

        }


        binding?.loginSignupBtn?.setOnClickListener {

            binding?.accountNotFoundTV?.visibility = View.GONE

            if(ifUserInputValidElseShowAppropriateMessage()){


                if(viewModel.loginSelected){
                    viewModel.checkUserExist(binding?.userEmailEditText?.text.toString())
                }
                else{
                    val user = User(
                        name = binding?.userNameEditText?.text.toString(),
                        email = binding?.userEmailEditText?.text.toString(),
                        password = binding?.userPasswordEditText?.text.toString()
                    )
                    viewModel.addUser(user)
                }
            }

        }
    }

    private fun initObserver(){

        viewModel.checkUserStatus?.observe(viewLifecycleOwner){
            when(it){
                ConstantData.ProgressStatuses.Loading ->{
                    binding?.mainLayout?.visibility = View.GONE
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                ConstantData.ProgressStatuses.Loaded ->{

                    // if user exist logic
                    if(viewModel.user!=null){

                        //if correct password
                        if(binding?.userPasswordEditText?.text.toString()== viewModel.user!!.password.toString()){
                            binding?.mainLayout?.visibility = View.VISIBLE
                            binding?.progressBar?.visibility = View.GONE
                            binding?.accountNotFoundTV?.visibility = View.GONE
                            sharedPreferences?.edit()?.apply {
                                putBoolean(SharedPreferencesTag.IS_LOGGED_IN,true)
                                putLong(SharedPreferencesTag.LOGGED_USER_ID, viewModel.user!!.id)
                                apply()
                            }

                            (requireActivity() as MainActivity).loadFragment(DetailFragment.newInstance())
                        }
                        // if in correct password
                        else{
                            binding?.mainLayout?.visibility = View.VISIBLE
                            binding?.progressBar?.visibility = View.GONE
                            binding?.accountNotFoundTV?.visibility = View.VISIBLE
                            binding?.accountNotFoundTV?.text = getString(R.string.incorrect_password)
                        }


                    }
                    //if user don't exist
                    else{
                        binding?.mainLayout?.visibility = View.VISIBLE
                        binding?.progressBar?.visibility = View.GONE
                        binding?.accountNotFoundTV?.visibility = View.VISIBLE
                        binding?.accountNotFoundTV?.text = getString(R.string.couldn_t_find_your_garage_account_try_to_sign_up_instead)
                    }
                }
                ConstantData.ProgressStatuses.Failed->{

                }
            }
        }

        viewModel.addUserStatus?.observe(viewLifecycleOwner){
            when(it){
                ConstantData.ProgressStatuses.Loading ->{
                    binding?.progressBar?.visibility=View.VISIBLE
                    binding?.mainLayout?.visibility = View.GONE
                }
                ConstantData.ProgressStatuses.Loaded ->{

                    binding?.progressBar?.visibility=View.GONE
                    binding?.mainLayout?.visibility = View.VISIBLE

                    //add details to shared preferences
                     sharedPreferences?.edit()?.apply {
                         putBoolean(SharedPreferencesTag.IS_LOGGED_IN,true)
                         putLong(SharedPreferencesTag.LOGGED_USER_ID, viewModel.userId!!)
                         commit()
                    }
                    //move to detail Fragment
                    (requireActivity() as MainActivity).loadFragment(DetailFragment.newInstance())
                }

                ConstantData.ProgressStatuses.Failed ->{
                }

            }

        }
    }

    fun ifUserInputValidElseShowAppropriateMessage(): Boolean {
        if(!viewModel.loginSelected){
            if (isNullOrEmptyOrTrimmedLengthIsZero(binding!!.userNameEditText.text.toString())) {
                binding!!.userNameInputLayout.error = "Please provide the User name"
                showAutomaticSoftFocusOrOpenKeyboardOnThisEditext(
                    context,
                    binding!!.userNameEditText
                )
                return false
            }
        }
        if (isInvalidEmail(binding!!.userEmailEditText.text.toString())) {
            binding!!.userEmailInputLayout.error =
                "Please provide valid Email"
            showAutomaticSoftFocusOrOpenKeyboardOnThisEditext(
                context,
                binding!!.userEmailEditText
            )
            return false
        }
        else if (isNullOrEmptyOrTrimmedLengthIsZero(binding!!.userPasswordEditText.text.toString())) {
            binding!!.userPasswordInputLayout.error =
                "Please provide your Address."
            showAutomaticSoftFocusOrOpenKeyboardOnThisEditext(
                context,
                binding!!.userPasswordEditText
            )
            return false
        }
        return true
    }
}