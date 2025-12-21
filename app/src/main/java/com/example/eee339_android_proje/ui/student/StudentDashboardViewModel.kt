package com.example.eee339_android_proje.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.Classroom
import com.example.eee339_android_proje.data.entity.ClassroomEnrollment
import kotlinx.coroutines.launch

class StudentDashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val classroomDao = AppDatabase.getDatabase(application).classroomDao()
    private val enrollmentDao = AppDatabase.getDatabase(application).classroomEnrollmentDao()
    private val announcementDao = AppDatabase.getDatabase(application).announcementDao()

    private val _studentId = MutableLiveData<Long>()

    private val _classrooms = MutableLiveData<List<Classroom>>()
    val classrooms: LiveData<List<Classroom>> = _classrooms

    private val _recentAnnouncements = MutableLiveData<List<com.example.eee339_android_proje.data.entity.Announcement>>()
    val recentAnnouncements: LiveData<List<com.example.eee339_android_proje.data.entity.Announcement>> = _recentAnnouncements

    private val _joinClassroomState = MutableLiveData<JoinClassroomState>()
    val joinClassroomState: LiveData<JoinClassroomState> = _joinClassroomState

    // Observer referanslarını sakla - memory leak önleme
    private var classroomsLiveData: LiveData<List<Classroom>>? = null
    private var classroomsObserver: Observer<List<Classroom>>? = null
    private var announcementsLiveData: LiveData<List<com.example.eee339_android_proje.data.entity.Announcement>>? = null
    private var announcementsObserver: Observer<List<com.example.eee339_android_proje.data.entity.Announcement>>? = null

    fun setStudentId(studentId: Long) {
        _studentId.value = studentId

        // Önceki observer'ları temizle
        cleanupObservers()

        // Kayıtlı sınıfları yükle
        viewModelScope.launch {
            try {
                // Biraz bekle ki veritabanı hazır olsun
                kotlinx.coroutines.delay(500)

                // Sınıfları getir
                classroomsLiveData = enrollmentDao.getEnrolledClassrooms(studentId)
                classroomsObserver = Observer { enrolledClassrooms ->
                    _classrooms.postValue(enrolledClassrooms)

                    // Duyuruları yükle
                    if (enrolledClassrooms.isNotEmpty()) {
                        val classroomIds = enrolledClassrooms.map { it.id }
                        
                        // Önceki duyuru observer'ını temizle
                        announcementsLiveData?.let { liveData ->
                            announcementsObserver?.let { observer ->
                                liveData.removeObserver(observer)
                            }
                        }
                        
                        announcementsLiveData = announcementDao.getRecentAnnouncementsByClassrooms(classroomIds)
                        announcementsObserver = Observer { announcements ->
                            _recentAnnouncements.postValue(announcements)
                        }
                        announcementsLiveData?.observeForever(announcementsObserver!!)
                    }
                }
                classroomsLiveData?.observeForever(classroomsObserver!!)
            } catch (e: Exception) {
                _classrooms.postValue(emptyList())
            }
        }
    }

    private fun cleanupObservers() {
        classroomsLiveData?.let { liveData ->
            classroomsObserver?.let { observer ->
                liveData.removeObserver(observer)
            }
        }
        announcementsLiveData?.let { liveData ->
            announcementsObserver?.let { observer ->
                liveData.removeObserver(observer)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanupObservers()
    }

    fun joinClassroom(joinCode: String) {
        if (joinCode.isBlank()) {
            _joinClassroomState.value = JoinClassroomState.Error("Lütfen katılım kodu girin")
            return
        }

        val studentId = _studentId.value ?: run {
            _joinClassroomState.value = JoinClassroomState.Error("Öğrenci ID bulunamadı")
            return
        }

        viewModelScope.launch {
            try {
                // Sınıfı bul
                val classroom = classroomDao.getClassroomByJoinCode(joinCode.uppercase())
                if (classroom == null) {
                    _joinClassroomState.postValue(JoinClassroomState.Error("Geçersiz katılım kodu"))
                    return@launch
                }

                // Zaten kayıtlı mı kontrol et
                val isEnrolled = enrollmentDao.isEnrolled(classroom.id, studentId) > 0
                if (isEnrolled) {
                    _joinClassroomState.postValue(JoinClassroomState.Error("Bu sınıfa zaten kayıtlısınız"))
                    return@launch
                }

                // Sınıfa kaydet
                val enrollment = ClassroomEnrollment(
                    classroomId = classroom.id,
                    studentId = studentId
                )

                val enrollmentId = enrollmentDao.enroll(enrollment)
                if (enrollmentId > 0) {
                    _joinClassroomState.postValue(JoinClassroomState.Success("${classroom.className} sınıfına başarıyla katıldınız!"))
                } else {
                    _joinClassroomState.postValue(JoinClassroomState.Error("Sınıfa katılırken hata oluştu"))
                }
            } catch (e: Exception) {
                _joinClassroomState.postValue(JoinClassroomState.Error("Hata: ${e.message}"))
            }
        }
    }

    sealed class JoinClassroomState {
        data class Success(val message: String) : JoinClassroomState()
        data class Error(val message: String) : JoinClassroomState()
    }
}

