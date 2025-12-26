package com.example.eee339_android_proje.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.User
import com.example.eee339_android_proje.databinding.ActivityLoginBinding
import com.example.eee339_android_proje.ui.student.StudentDashboardActivity
import com.example.eee339_android_proje.ui.teacher.TeacherDashboardActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        observeViewModel()
        checkAndInitializeDatabase()
    }

    private fun checkAndInitializeDatabase() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                // Veritabanının açılması ve demo verilerin yüklenmesi için bekleme
                kotlinx.coroutines.delay(2000)

                var retryCount = 0
                while (retryCount < 5) {
                    val userCount = db.userDao().getUserCount()
                    if (userCount > 0) {
                        // Veriler yüklendi
                        binding.tvError.visibility = View.GONE
                        break
                    }
                    retryCount++
                    kotlinx.coroutines.delay(500)
                }

                if (retryCount >= 5) {
                    binding.tvError.text = "Veritabanı yükleniyor... Lütfen uygulamayı yeniden başlatın."
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            } catch (e: Exception) {
                binding.tvError.text = "Veritabanı hatası: ${e.message}"
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.setTextColor(getColor(android.R.color.holo_red_dark))
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(username, password)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, com.example.eee339_android_proje.ui.register.RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.etUsername.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
        }

        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    navigateToDashboard(state.user)
                }
                is LoginViewModel.LoginState.Error -> {
                    binding.tvError.text = state.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToDashboard(user: User) {
        val intent = when (user.role) {
            User.ROLE_TEACHER -> Intent(this, TeacherDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }
        intent.putExtra(EXTRA_USER_ID, user.id)
        intent.putExtra(EXTRA_USER_ROLE, user.role)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_USER_ROLE = "extra_user_role"
    }
}
