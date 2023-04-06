package com.scouto.assignment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.scouto.assignment.databinding.ActivityMainBinding
import com.scouto.assignment.fragment.DetailFragment
import com.scouto.assignment.fragment.LoginSignUpFragment
import com.scouto.assignment.utils.SharedPreferencesTag
import com.scouto.assignment.utils.getRandomString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private val sharedPreferences by lazy {
        getSharedPreferences(SharedPreferencesTag.SHARED_PREF_NAME,Context.MODE_PRIVATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(sharedPreferences.getBoolean(SharedPreferencesTag.IS_LOGGED_IN,false)){
            loadFragment(DetailFragment.newInstance())
        }
        else{
            loadFragment(LoginSignUpFragment.newInstance())
        }

    }

    fun loadFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_left,
            R.anim.slide_right,
            R.anim.slide_left,
            R.anim.slide_right
        )
        fragmentTransaction.replace(
            binding.containerForFragment.id,
            fragment,
            getRandomString(8)
        )

        fragmentTransaction.commit()

    }
}