package com.example.eee339_android_proje.ui.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.Classroom
import kotlinx.coroutines.launch

class TeacherDashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val classroomDao = AppDatabase.getDatabase(application).classroomDao()
    private val announcementDao = AppDatabase.getDatabase(application).announcementDao()

    private val _teacherId = MutableLiveData<Long>()
    
    private var _classrooms: LiveData<List<Classroom>>? = null
    val classrooms: LiveData<List<Classroom>>
        get() = _classrooms ?: classroomDao.getAllClassrooms()
    
    private val _createClassroomState = MutableLiveData<CreateClassroomState>()
    val createClassroomState: LiveData<CreateClassroomState> = _createClassroomState

    private val _createAnnouncementState = MutableLiveData<CreateAnnouncementState>()
    val createAnnouncementState: LiveData<CreateAnnouncementState> = _createAnnouncementState

    fun setTeacherId(teacherId: Long) {
        _teacherId.value = teacherId
        _classrooms = classroomDao.getClassroomsByTeacher(teacherId)
    }

    fun createClassroom(className: String, description: String, joinCode: String) {
        if (className.isBlank()) {
            _createClassroomState.value = CreateClassroomState.Error("Lütfen sınıf adı girin")
            return
        }

        if (joinCode.isBlank()) {
            _createClassroomState.value = CreateClassroomState.Error("Lütfen katılım kodu girin")
            return
        }

        val teacherId = _teacherId.value ?: run {
            _createClassroomState.value = CreateClassroomState.Error("Öğretmen ID bulunamadı")
            return
        }

        viewModelScope.launch {
            try {
                // Katılım kodunun benzersiz olup olmadığını kontrol et
                val existingClassroom = classroomDao.getClassroomByJoinCode(joinCode.uppercase())
                if (existingClassroom != null) {
                    _createClassroomState.postValue(CreateClassroomState.Error("Bu katılım kodu zaten kullanılıyor"))
                    return@launch
                }

                val classroom = Classroom(
                    className = className,
                    description = description,
                    teacherId = teacherId,
                    joinCode = joinCode.uppercase()
                )

                val classroomId = classroomDao.insert(classroom)
                if (classroomId > 0) {
                    _createClassroomState.postValue(CreateClassroomState.Success("Sınıf başarıyla oluşturuldu!"))
                } else {
                    _createClassroomState.postValue(CreateClassroomState.Error("Sınıf oluşturulurken hata oluştu"))
                }
            } catch (e: Exception) {
                _createClassroomState.postValue(CreateClassroomState.Error("Hata: ${e.message}"))
            }
        }
    }

    fun createAnnouncement(classroomId: Long, title: String, message: String) {
        if (title.isBlank()) {
            _createAnnouncementState.value = CreateAnnouncementState.Error("Lütfen duyuru başlığı girin")
            return
        }

        if (message.isBlank()) {
            _createAnnouncementState.value = CreateAnnouncementState.Error("Lütfen duyuru mesajı girin")
            return
        }

        val teacherId = _teacherId.value ?: run {
            _createAnnouncementState.value = CreateAnnouncementState.Error("Öğretmen ID bulunamadı")
            return
        }

        viewModelScope.launch {
            try {
                val announcement = com.example.eee339_android_proje.data.entity.Announcement(
                    classroomId = classroomId,
                    teacherId = teacherId,
                    title = title,
                    message = message
                )

                val announcementId = announcementDao.insert(announcement)
                if (announcementId > 0) {
                    _createAnnouncementState.postValue(CreateAnnouncementState.Success("Duyuru başarıyla paylaşıldı!"))
                } else {
                    _createAnnouncementState.postValue(CreateAnnouncementState.Error("Duyuru paylaşılırken hata oluştu"))
                }
            } catch (e: Exception) {
                _createAnnouncementState.postValue(CreateAnnouncementState.Error("Hata: ${e.message}"))
            }
        }
    }

    sealed class CreateClassroomState {
        data class Success(val message: String) : CreateClassroomState()
        data class Error(val message: String) : CreateClassroomState()
    }

    sealed class CreateAnnouncementState {
        data class Success(val message: String) : CreateAnnouncementState()
        data class Error(val message: String) : CreateAnnouncementState()
    }
}
