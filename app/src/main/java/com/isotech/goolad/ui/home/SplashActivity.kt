package com.isotech.goolad.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.isotech.goolad.MainActivity

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import com.isotech.goolad.ui.auth.AuthViewModel
import com.isotech.goolad.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val authViewModel by lazy {
        ViewModelProviders.of(this)[AuthViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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

    private fun userNotLoggedIn() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun userLoggedIn() {
        Intent(this, HomeActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}