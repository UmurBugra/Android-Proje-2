package com.example.eee339_android_proje.ui.classroom

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eee339_android_proje.databinding.ActivityClassroomDetailBinding
import com.example.eee339_android_proje.ui.adapter.CaseAdapter
import com.example.eee339_android_proje.ui.login.LoginActivity
import com.example.eee339_android_proje.ui.simulation.CaseSimulationActivity

class ClassroomDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassroomDetailBinding
    private val viewModel: ClassroomDetailViewModel by viewModels()
    private lateinit var adapter: CaseAdapter

    private var classroomId: Long = 0
    private var userId: Long = 0
    private var isTeacher: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityClassroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        classroomId = intent.getLongExtra(EXTRA_CLASSROOM_ID, 0)
        userId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, 0)
        isTeacher = intent.getBooleanExtra("is_teacher", false)

        viewModel.setClassroomId(classroomId)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CaseAdapter { caseScenario ->
            val intent = Intent(this, CaseSimulationActivity::class.java)
            intent.putExtra(CaseSimulationActivity.EXTRA_CASE_ID, caseScenario.id)
            intent.putExtra(LoginActivity.EXTRA_USER_ID, userId)
            intent.putExtra("is_teacher", isTeacher)
            startActivity(intent)
        }
        binding.rvCases.layoutManager = LinearLayoutManager(this)
        binding.rvCases.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.classroom.observe(this) { classroom ->
            classroom?.let {
                binding.tvTitle.text = it.className
            }
        }

        viewModel.cases.observe(this) { cases ->
            adapter.submitList(cases)
            binding.tvEmpty.visibility = if (cases.isEmpty()) View.VISIBLE else View.GONE
            binding.rvCases.visibility = if (cases.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_CLASSROOM_ID = "extra_classroom_id"
    }
}
