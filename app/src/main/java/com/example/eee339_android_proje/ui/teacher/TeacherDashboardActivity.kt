package com.example.eee339_android_proje.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eee339_android_proje.databinding.ActivityTeacherDashboardBinding
import com.example.eee339_android_proje.ui.adapter.ClassroomAdapter
import com.example.eee339_android_proje.ui.classroom.ClassroomDetailActivity
import com.example.eee339_android_proje.ui.login.LoginActivity

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private val viewModel: TeacherDashboardViewModel by viewModels()
    private lateinit var adapter: ClassroomAdapter

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, 0)
        viewModel.setTeacherId(userId)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ClassroomAdapter { classroom ->
            val intent = Intent(this, ClassroomDetailActivity::class.java)
            intent.putExtra(ClassroomDetailActivity.EXTRA_CLASSROOM_ID, classroom.id)
            intent.putExtra(LoginActivity.EXTRA_USER_ID, userId)
            intent.putExtra("is_teacher", true)
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

        binding.btnNewClass.setOnClickListener {
            Toast.makeText(this, "Yeni sınıf oluşturma özelliği yakında!", Toast.LENGTH_SHORT).show()
        }

        binding.btnNewAnnouncement.setOnClickListener {
            Toast.makeText(this, "Duyuru yapma özelliği yakında!", Toast.LENGTH_SHORT).show()
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
