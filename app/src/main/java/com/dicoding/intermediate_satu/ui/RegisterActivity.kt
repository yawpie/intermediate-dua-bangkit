package com.dicoding.intermediate_satu.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediate_satu.databinding.ActivityRegisterBinding
import com.dicoding.intermediate_satu.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        binding.chooseLogin.setOnClickListener {
            goToLogin()
        }
        viewModel.isLoading.observe(this) {
            binding.progressBarRegister.visibility = if (it) View.VISIBLE else View.GONE
        }
        val submitButton = binding.submitButton
        val passwordBinding = binding.edRegisterPassword


        playTogether(binding.dicodingLogo)
        playAnimations(
            binding.textview,
            binding.edRegisterName,
            binding.edRegisterEmail,
            binding.edRegisterPassword,
            binding.chooseLogin,
            submitButton
        )
        submitButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        val name = binding.edRegisterName.text.toString()
        viewModel.register(this, name, email, password)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}