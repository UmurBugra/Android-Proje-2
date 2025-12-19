package com.example.eee339_android_proje.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eee339_android_proje.data.entity.User
import com.example.eee339_android_proje.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            val role = if (binding.rbTeacher.isChecked) {
                User.ROLE_TEACHER
            } else {
                User.ROLE_STUDENT
            }

            viewModel.register(username, password, confirmPassword, role)
        }

        // Giriş ekranına dön
        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
            binding.etUsername.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
            binding.etConfirmPassword.isEnabled = !isLoading
            binding.rbStudent.isEnabled = !isLoading
            binding.rbTeacher.isEnabled = !isLoading
        }

        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterViewModel.RegisterState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    // Başarılı kayıt sonrası giriş ekranına dön
                    finish()
                }
                is RegisterViewModel.RegisterState.Error -> {
                    binding.tvError.text = state.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}

