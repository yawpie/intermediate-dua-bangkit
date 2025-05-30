package com.dicoding.intermediate_satu.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediate_satu.data.User
import com.dicoding.intermediate_satu.databinding.ActivityLoginBinding
import com.dicoding.intermediate_satu.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chooseRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.isLoading.observe(this) {
            binding.progressBarLogin.visibility = if (it) View.VISIBLE else View.GONE
        }
        val submitButton = binding.submitButton
        val passwordBinding = binding.edLoginPassword

        playTogether(binding.dicodingLogo, binding.titleApp)
        playAnimations(
            binding.textview,
            binding.edLoginEmail,
            binding.edLoginPassword,
            binding.chooseRegister,
            submitButton
        )


        binding.submitButton.setOnClickListener {
            login()
        }


    }

    private fun login() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.getText().toString()
        var userResult: User? = null
        CoroutineScope(Dispatchers.Main).launch {
            userResult = viewModel.login(this@LoginActivity, email, password)

        }.invokeOnCompletion {
            if (userResult != null) {
                val intent = Intent(this, StoriesActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } else {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserData() {
        viewModel.getUserFromRepo(this)
    }
}