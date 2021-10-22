package com.isotech.goolad.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.isotech.goolad.R
import com.isotech.goolad.data.model.User
import com.isotech.goolad.databinding.ActivityMainBinding
import com.isotech.goolad.databinding.ActivityRegisterBinding
import com.isotech.goolad.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initObservers()
    }

    private fun initViews() {
        with(binding) {
            inputName.addTextChangedListener(registerTextWatcher)
            inputEmail.addTextChangedListener(registerTextWatcher)
            inputPassword.addTextChangedListener(registerTextWatcher)
            btnRegister.setOnClickListener {
                authViewModel.registerLoading.value = true
                user = User(
                    "",
                    input_name.text.toString(),
                    input_email.text.toString(),
                    input_phone.text.toString(),
                )
                authViewModel.createUserWithEmailPass(user, input_password.text.toString())
            }
        }
        binding.textLoginRight.setOnClickListener {
            Intent(this, LoginActivity::class.java).apply {
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
        authViewModel.registerLoading.observe(this, { isLoading ->
            if (isLoading && !loadingDialog.isShowing) {
                loadingDialog.show()
            } else if (!isLoading && loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        })
    }

    private val registerTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding) {
                val nameInput = inputName.text.toString().trim()
                val usernameInput = inputEmail.text.toString().trim()
                val passwordInput = inputPassword.text.toString().trim()
                val phoneInput = inputPassword.text.toString().trim()
                btnRegister.isEnabled =
                    nameInput.isNotEmpty() && usernameInput.isNotEmpty() && passwordInput.length > 5 && phoneInput.isNotEmpty()
            }

        }

        override fun afterTextChanged(s: Editable) {

        }
    }
}