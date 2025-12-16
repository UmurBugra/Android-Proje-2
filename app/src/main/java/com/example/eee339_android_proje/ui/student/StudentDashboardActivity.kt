package com.example.eee339_android_proje.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eee339_android_proje.databinding.ActivityStudentDashboardBinding
import com.example.eee339_android_proje.ui.adapter.ClassroomAdapter
import com.example.eee339_android_proje.ui.classroom.ClassroomDetailActivity
import com.example.eee339_android_proje.ui.login.LoginActivity

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private val viewModel: StudentDashboardViewModel by viewModels()
    private lateinit var adapter: ClassroomAdapter

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, 0)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ClassroomAdapter { classroom ->
            val intent = Intent(this, ClassroomDetailActivity::class.java)
            intent.putExtra(ClassroomDetailActivity.EXTRA_CLASSROOM_ID, classroom.id)
            intent.putExtra(LoginActivity.EXTRA_USER_ID, userId)
            intent.putExtra("is_teacher", false)
            startActivity(intent)
        }
        binding.rvClassrooms.layoutManager = LinearLayoutManager(this)
        binding.rvClassrooms.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.classrooms.observe(this) { classrooms ->
            adapter.submitList(classrooms)
            binding.tvEmpty.visibility = if (classrooms.isEmpty()) View.VISIBLE else View.GONE
            binding.rvClassrooms.visibility = if (classrooms.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
