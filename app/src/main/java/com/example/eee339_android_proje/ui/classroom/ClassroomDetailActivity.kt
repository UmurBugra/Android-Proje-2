package com.example.eee339_android_proje.ui.classroom

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.databinding.ActivityClassroomDetailBinding
import com.example.eee339_android_proje.databinding.DialogGradeDiagnosisBinding
import com.example.eee339_android_proje.ui.adapter.CaseAdapter
import com.example.eee339_android_proje.ui.login.LoginActivity
import com.example.eee339_android_proje.ui.simulation.CaseSimulationActivity
import com.example.eee339_android_proje.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        // Öğretmen ise tanı görüntüleme butonunu göster
        if (isTeacher) {
            binding.btnViewDiagnoses.visibility = View.VISIBLE
        }

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

        binding.btnViewDiagnoses.setOnClickListener {
            showDiagnosesDialog()
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

        viewModel.announcements.observe(this) { announcements ->
            if (announcements.isNotEmpty()) {
                val announcementText = announcements.joinToString("\n\n") { announcement ->
                    "📢 ${announcement.title}\n${announcement.message}"
                }
                binding.tvAnnouncements.text = announcementText
            } else {
                binding.tvAnnouncements.text = getString(com.example.eee339_android_proje.R.string.no_announcements)
            }
        }
    }

    private fun showDiagnosesDialog() {
        val diagnosisDao = AppDatabase.getDatabase(applicationContext).studentDiagnosisDao()
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        val caseDao = AppDatabase.getDatabase(applicationContext).caseScenarioDao()

        diagnosisDao.getDiagnosesByClassroom(classroomId).observe(this@ClassroomDetailActivity) { diagnoses ->
            if (diagnoses.isEmpty()) {
                Toast.makeText(this@ClassroomDetailActivity, getString(R.string.no_diagnoses), Toast.LENGTH_SHORT).show()
                return@observe
            }

            // Verileri asenkron olarak yükle ve UI'ı güncelle
            lifecycleScope.launch {
                val items = diagnoses.map { diagnosis ->
                    val student = userDao.getUserById(diagnosis.studentId)
                    val case = caseDao.getCaseById(diagnosis.caseId)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("tr", "TR"))
                    val date = dateFormat.format(Date(diagnosis.submittedAt))
                    val scoreText = if (diagnosis.score != null) "${diagnosis.score}/100" else getString(R.string.not_graded_yet)

                    "${student?.username ?: "?"} - ${case?.title ?: "?"}\n$date - $scoreText"
                }.toTypedArray()

                AlertDialog.Builder(this@ClassroomDetailActivity)
                    .setTitle(getString(R.string.student_diagnoses_title))
                    .setItems(items) { _, which ->
                        showGradeDiagnosisDialog(diagnoses[which])
                    }
                    .setNegativeButton(getString(R.string.btn_close), null)
                    .show()
            }
        }
    }

    private fun showGradeDiagnosisDialog(diagnosis: com.example.eee339_android_proje.data.entity.StudentDiagnosis) {
        val dialogBinding = DialogGradeDiagnosisBinding.inflate(LayoutInflater.from(this))

        lifecycleScope.launch {
            val userDao = AppDatabase.getDatabase(applicationContext).userDao()
            val caseDao = AppDatabase.getDatabase(applicationContext).caseScenarioDao()

            val student = userDao.getUserById(diagnosis.studentId)
            val case = caseDao.getCaseById(diagnosis.caseId)

            dialogBinding.tvStudentInfo.text = "Öğrenci: ${student?.username ?: "?"}\nVaka: ${case?.title ?: "?"}"
            dialogBinding.tvCorrectDiagnosis.text = "Doğru Tanı: ${case?.correctDiagnosis ?: "?"}"
            dialogBinding.tvStudentDiagnosis.text = diagnosis.diagnosis
            dialogBinding.tvExplanation.text = diagnosis.explanation

            if (diagnosis.score != null) {
                dialogBinding.etScore.setText(diagnosis.score.toString())
            }
            if (diagnosis.teacherFeedback != null) {
                dialogBinding.etFeedback.setText(diagnosis.teacherFeedback)
            }

            val dialog = AlertDialog.Builder(this@ClassroomDetailActivity)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.btnMarkCorrect.setOnClickListener {
                dialogBinding.etScore.setText("100")
            }

            dialogBinding.btnMarkIncorrect.setOnClickListener {
                dialogBinding.etScore.setText("0")
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnSaveGrade.setOnClickListener {
                val scoreText = dialogBinding.etScore.text.toString()
                val feedback = dialogBinding.etFeedback.text.toString()

                if (scoreText.isEmpty()) {
                    Toast.makeText(this@ClassroomDetailActivity, getString(R.string.error_score_empty), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val score = scoreText.toIntOrNull()
                if (score == null || score < 0 || score > 100) {
                    Toast.makeText(this@ClassroomDetailActivity, getString(R.string.error_score_invalid), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    val diagnosisDao = AppDatabase.getDatabase(applicationContext).studentDiagnosisDao()
                    val updatedDiagnosis = diagnosis.copy(
                        isCorrect = score >= 50,
                        score = score,
                        teacherFeedback = feedback.ifEmpty { null },
                        gradedByTeacherId = userId,
                        gradedAt = System.currentTimeMillis()
                    )
                    diagnosisDao.update(updatedDiagnosis)

                    Toast.makeText(this@ClassroomDetailActivity, getString(R.string.diagnosis_graded_success), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            dialog.show()
        }
    }

    companion object {
        const val EXTRA_CLASSROOM_ID = "extra_classroom_id"
    }
}
