package com.isotech.goolad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.isotech.goolad.databinding.ActivityMainBinding
import com.isotech.goolad.ui.auth.AuthViewModel
import com.isotech.goolad.ui.auth.LoginActivity
import com.isotech.goolad.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel by lazy {
        ViewModelProviders.of(this)[AuthViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()
    }

    private fun initObservers() {
        authViewModel.getCurrentUser().observe(this, { firebaseUser ->
            if (firebaseUser == null) {
                userNotLoggedIn()
            } else {
                userLoggedIn()
            }
        })
    }

    private fun userLoggedIn() {
        Intent(this, HomeActivity::class.java).apply {
            startActivity(this)
            finish()
        }
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.main_container, HomeFragment())
//            .commit()
    }

     fun userNotLoggedIn() {
         Intent(this, LoginActivity::class.java).apply {
             startActivity(this)
             finish()
         }
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.main_container, LoginFragment())
//            .commit()
    }
}