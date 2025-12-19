package com.example.eee339_android_proje.ui.simulation

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eee339_android_proje.databinding.ActivityCaseSimulationBinding
import com.example.eee339_android_proje.databinding.DialogSubmitDiagnosisBinding
import com.example.eee339_android_proje.ui.adapter.SimulationLogAdapter
import com.example.eee339_android_proje.ui.login.LoginActivity

class CaseSimulationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaseSimulationBinding
    private val viewModel: CaseSimulationViewModel by viewModels()
    private lateinit var logAdapter: SimulationLogAdapter

    private var caseId: Long = 0
    private var userId: Long = 0
    private var isTeacher: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCaseSimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        caseId = intent.getLongExtra(EXTRA_CASE_ID, 0)
        userId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, 0)
        isTeacher = intent.getBooleanExtra("is_teacher", false)

        viewModel.initialize(caseId, userId)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Öğretmen modunda butonları gizle
        if (isTeacher) {
            binding.actionPanel.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        logAdapter = SimulationLogAdapter()
        binding.rvLogs.layoutManager = LinearLayoutManager(this)
        binding.rvLogs.adapter = logAdapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRequestTest.setOnClickListener {
            viewModel.performAction("Tahlil İste")
        }

        binding.btnExamine.setOnClickListener {
            viewModel.performAction("Muayene Et")
        }

        binding.btnDiagnose.setOnClickListener {
            showSubmitDiagnosisDialog()
        }

        binding.btnPrescribe.setOnClickListener {
            viewModel.performAction("Reçete Yaz")
        }
    }

    private fun observeViewModel() {
        viewModel.caseScenario.observe(this) { caseScenario ->
            caseScenario?.let {
                binding.tvCaseTitle.text = it.title
                binding.tvPatientInfo.text = it.patientInfo
                viewModel.parseVitalSigns(it.vitalSigns)
            }
        }

        viewModel.vitalSigns.observe(this) { vitals ->
            binding.tvTemperature.text = vitals.temperature
            binding.tvPulse.text = vitals.pulse
            binding.tvBloodPressure.text = vitals.bloodPressure
            binding.tvRespiratory.text = vitals.respiratory
        }

        viewModel.logs.observe(this) { logs ->
            logAdapter.submitList(logs)
            binding.tvEmptyLogs.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
            binding.rvLogs.visibility = if (logs.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.diagnosisSubmitState.observe(this) { state ->
            when (state) {
                is CaseSimulationViewModel.DiagnosisSubmitState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is CaseSimulationViewModel.DiagnosisSubmitState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSubmitDiagnosisDialog() {
        val dialogBinding = DialogSubmitDiagnosisBinding.inflate(LayoutInflater.from(this))

        // Doğru tanıyı göster (isteğe bağlı - debug için)
        viewModel.caseScenario.value?.let { case ->
            dialogBinding.tvCorrectDiagnosis.visibility = View.VISIBLE
            dialogBinding.tvCorrectDiagnosis.text = "Not: Doğru tanı - ${case.correctDiagnosis}"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val diagnosis = dialogBinding.etDiagnosis.text.toString().trim()
            val explanation = dialogBinding.etExplanation.text.toString().trim()

            if (diagnosis.isEmpty()) {
                Toast.makeText(this, "Lütfen tanı girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (explanation.isEmpty()) {
                Toast.makeText(this, "Lütfen tanı açıklaması girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.submitDiagnosis(caseId, userId, diagnosis, explanation)
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        const val EXTRA_CASE_ID = "extra_case_id"
    }
}
