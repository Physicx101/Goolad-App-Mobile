package com.isotech.goolad.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.isotech.goolad.R
import com.isotech.goolad.data.model.User
import com.isotech.goolad.databinding.ActivityLoginBinding
import com.isotech.goolad.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel by lazy {
        ViewModelProviders.of(this)[AuthViewModel::class.java]
    }

    private val loadingDialog: AlertDialog by lazy {
        val dialog = AlertDialog.Builder(this)
            .setView(this.layoutInflater.inflate(R.layout.dialog_loading, null))
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog
    }

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initObservers()
    }

    private fun initViews() {
        with(binding) {
            inputEmail.addTextChangedListener(loginTextWatcher)
            inputPassword.addTextChangedListener(loginTextWatcher)
            btnLogin.setOnClickListener {
                val email = input_email.text.toString()
                val password = input_password.text.toString()
                authViewModel.loginLoading.value = true
                authViewModel.signInWithEmailPass(email, password)
            }
        }
        binding.textSignupRight.setOnClickListener {
            Intent(this, RegisterActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
    }

    private fun initObservers() {
        authViewModel.getCurrentUser().observe(this, { firebaseUser ->
            if (firebaseUser != null) {
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        })
        authViewModel.error.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        authViewModel.loginLoading.observe(this, { isLoading ->
            if (isLoading && !loadingDialog.isShowing) {
                loadingDialog.show()
            } else if (!isLoading && loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        })
    }

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val usernameInput = input_email.text.toString().trim()
            val passwordInput = input_password.text.toString().trim()
            btn_login.isEnabled = usernameInput.isNotEmpty() && passwordInput.length > 5
        }

        override fun afterTextChanged(s: Editable) {

        }
    }
}