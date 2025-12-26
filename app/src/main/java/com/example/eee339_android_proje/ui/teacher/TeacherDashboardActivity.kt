package com.example.eee339_android_proje.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eee339_android_proje.databinding.ActivityTeacherDashboardBinding
import com.example.eee339_android_proje.databinding.DialogCreateAnnouncementBinding
import com.example.eee339_android_proje.databinding.DialogCreateClassroomBinding
import com.example.eee339_android_proje.ui.adapter.ClassroomAdapter
import com.example.eee339_android_proje.ui.classroom.ClassroomDetailActivity
import com.example.eee339_android_proje.ui.login.LoginActivity
import com.example.eee339_android_proje.R

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
            showCreateClassroomDialog()
        }

        binding.btnNewAnnouncement.setOnClickListener {
            showCreateAnnouncementDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.classrooms.observe(this) { classrooms ->
            adapter.submitList(classrooms)
            binding.tvEmpty.visibility = if (classrooms.isEmpty()) View.VISIBLE else View.GONE
            binding.rvClassrooms.visibility = if (classrooms.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.createClassroomState.observe(this) { state ->
            when (state) {
                is TeacherDashboardViewModel.CreateClassroomState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is TeacherDashboardViewModel.CreateClassroomState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.createAnnouncementState.observe(this) { state ->
            when (state) {
                is TeacherDashboardViewModel.CreateAnnouncementState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is TeacherDashboardViewModel.CreateAnnouncementState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCreateClassroomDialog() {
        val dialogBinding = DialogCreateClassroomBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnCreate.setOnClickListener {
            val className = dialogBinding.etClassName.text.toString().trim()
            val joinCode = dialogBinding.etJoinCode.text.toString().trim()
            val description = dialogBinding.etDescription.text.toString().trim()

            viewModel.createClassroom(className, description, joinCode)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCreateAnnouncementDialog() {
        val dialogBinding = DialogCreateAnnouncementBinding.inflate(LayoutInflater.from(this))

        // Sınıfları gözlemle ve spinner'a ekle
        viewModel.classrooms.observe(this) { classrooms ->
            if (classrooms.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_create_classroom_first), Toast.LENGTH_SHORT).show()
                return@observe
            }

            val classroomNames = classrooms.map { it.className }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classroomNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerClassroom.adapter = adapter
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnPost.setOnClickListener {
            val selectedPosition = dialogBinding.spinnerClassroom.selectedItemPosition
            val title = dialogBinding.etAnnouncementTitle.text.toString().trim()
            val message = dialogBinding.etAnnouncementMessage.text.toString().trim()

            viewModel.classrooms.value?.let { classrooms ->
                if (selectedPosition >= 0 && selectedPosition < classrooms.size) {
                    val selectedClassroom = classrooms[selectedPosition]
                    viewModel.createAnnouncement(selectedClassroom.id, title, message)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}
