package com.example.eee339_android_proje.ui.simulation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.CaseScenario
import com.example.eee339_android_proje.data.entity.SimulationLog
import kotlinx.coroutines.launch
import org.json.JSONObject

class CaseSimulationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val caseScenarioDao = AppDatabase.getDatabase(application).caseScenarioDao()
    private val simulationLogDao = AppDatabase.getDatabase(application).simulationLogDao()
    private val studentDiagnosisDao = AppDatabase.getDatabase(application).studentDiagnosisDao()

    private var _case: LiveData<CaseScenario?>? = null
    val caseScenario: LiveData<CaseScenario?>
        get() = _case ?: MutableLiveData(null)
    
    private var _logs: LiveData<List<SimulationLog>>? = null
    val logs: LiveData<List<SimulationLog>>
        get() = _logs ?: MutableLiveData(emptyList())
    
    private var _existingDiagnosis: LiveData<com.example.eee339_android_proje.data.entity.StudentDiagnosis?>? = null
    val existingDiagnosis: LiveData<com.example.eee339_android_proje.data.entity.StudentDiagnosis?>
        get() = _existingDiagnosis ?: MutableLiveData(null)

    private val _vitalSigns = MutableLiveData<VitalSigns>()
    val vitalSigns: LiveData<VitalSigns> = _vitalSigns
    
    private val _diagnosisSubmitState = MutableLiveData<DiagnosisSubmitState>()
    val diagnosisSubmitState: LiveData<DiagnosisSubmitState> = _diagnosisSubmitState

    private var caseId: Long = 0
    private var studentId: Long = 0
    
    fun initialize(caseId: Long, studentId: Long) {
        this.caseId = caseId
        this.studentId = studentId
        _case = caseScenarioDao.getCaseByIdLive(caseId)
        _logs = simulationLogDao.getLogsByCase(caseId, studentId)
        _existingDiagnosis = studentDiagnosisDao.getDiagnosisByStudentAndCaseLive(caseId, studentId)
    }
    
    fun parseVitalSigns(vitalSignsJson: String) {
        try {
            val json = JSONObject(vitalSignsJson)
            _vitalSigns.value = VitalSigns(
                temperature = json.optString("ates", "-"),
                pulse = json.optString("nabiz", "-"),
                bloodPressure = json.optString("tansiyon", "-"),
                respiratory = json.optString("solunum", "-")
            )
        } catch (e: Exception) {
            _vitalSigns.value = VitalSigns("-", "-", "-", "-")
        }
    }
    
    fun performAction(actionName: String) {
        viewModelScope.launch {
            val feedback = generateFeedback(actionName)
            val log = SimulationLog(
                studentId = studentId,
                caseId = caseId,
                actionName = actionName,
                feedbackMessage = feedback
            )
            simulationLogDao.insert(log)
        }
    }
    
    private fun generateFeedback(actionName: String): String {
        return when (actionName) {
            "Tahlil İste" -> listOf(
                "Kan tahlili sonuçları bekleniyor...",
                "Hemogram ve biyokimya paneli istendi.",
                "Acil kan gazı sonucu: pH 7.38, pCO2 42 mmHg"
            ).random()
            "Muayene Et" -> listOf(
                "Fizik muayene: Batın hassasiyeti saptandı.",
                "Oskültasyon: Kalp sesleri normal, akciğerler temiz.",
                "Nörolojik muayene: Bilinç açık, oryante, koopere."
            ).random()
            "Tanı Koy" -> listOf(
                "Tanı kaydedildi. Doğru tanı ile karşılaştırılacak.",
                "Ayırıcı tanı listesi güncellendi."
            ).random()
            "Reçete Yaz" -> listOf(
                "Tedavi planı oluşturuldu.",
                "İlaç dozajı ve kullanım şekli belirlendi."
            ).random()
            else -> "İşlem kaydedildi."
        }
    }
    
    fun submitDiagnosis(caseId: Long, studentId: Long, diagnosis: String, explanation: String) {
        viewModelScope.launch {
            try {
                // Önce bu öğrencinin bu vakaya daha önce tanı koyup koymadığını kontrol et
                val existingDiagnosis = studentDiagnosisDao.getDiagnosisByStudentAndCase(caseId, studentId)

                if (existingDiagnosis != null) {
                    // Varolan tanıyı güncelle
                    val updatedDiagnosis = existingDiagnosis.copy(
                        diagnosis = diagnosis,
                        explanation = explanation,
                        submittedAt = System.currentTimeMillis(),
                        // Yeniden gönderildiği için puanı sıfırla
                        isCorrect = null,
                        score = null,
                        teacherFeedback = null,
                        gradedByTeacherId = null,
                        gradedAt = null
                    )
                    studentDiagnosisDao.update(updatedDiagnosis)
                    performAction("Tanı Güncelle: $diagnosis")
                    _diagnosisSubmitState.postValue(DiagnosisSubmitState.Success("Tanınız başarıyla güncellendi!"))
                } else {
                    // Yeni tanı ekle
                    val studentDiagnosis = com.example.eee339_android_proje.data.entity.StudentDiagnosis(
                        caseId = caseId,
                        studentId = studentId,
                        diagnosis = diagnosis,
                        explanation = explanation
                    )

                    val diagnosisId = studentDiagnosisDao.insert(studentDiagnosis)
                    if (diagnosisId > 0) {
                        performAction("Tanı Koy: $diagnosis")
                        _diagnosisSubmitState.postValue(DiagnosisSubmitState.Success("Tanınız başarıyla gönderildi!"))
                    } else {
                        _diagnosisSubmitState.postValue(DiagnosisSubmitState.Error("Tanı gönderilirken hata oluştu"))
                    }
                }
            } catch (e: Exception) {
                _diagnosisSubmitState.postValue(DiagnosisSubmitState.Error("Hata: ${e.message}"))
            }
        }
    }

    data class VitalSigns(
        val temperature: String,
        val pulse: String,
        val bloodPressure: String,
        val respiratory: String
    )

    sealed class DiagnosisSubmitState {
        data class Success(val message: String) : DiagnosisSubmitState()
        data class Error(val message: String) : DiagnosisSubmitState()
    }
}
