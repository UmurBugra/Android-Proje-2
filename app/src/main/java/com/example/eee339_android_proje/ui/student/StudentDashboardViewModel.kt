package com.example.eee339_android_proje.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.Classroom

class StudentDashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val classroomDao = AppDatabase.getDatabase(application).classroomDao()
    
    val classrooms: LiveData<List<Classroom>> = classroomDao.getAllClassrooms()
}
