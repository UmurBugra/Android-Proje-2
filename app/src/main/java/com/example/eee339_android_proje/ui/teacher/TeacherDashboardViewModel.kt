package com.example.eee339_android_proje.ui.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.Classroom

class TeacherDashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val classroomDao = AppDatabase.getDatabase(application).classroomDao()
    
    private val _teacherId = MutableLiveData<Long>()
    
    private var _classrooms: LiveData<List<Classroom>>? = null
    val classrooms: LiveData<List<Classroom>>
        get() = _classrooms ?: classroomDao.getAllClassrooms()
    
    fun setTeacherId(teacherId: Long) {
        _teacherId.value = teacherId
        _classrooms = classroomDao.getClassroomsByTeacher(teacherId)
    }
}
