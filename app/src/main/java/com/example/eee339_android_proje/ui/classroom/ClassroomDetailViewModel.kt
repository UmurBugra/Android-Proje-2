package com.example.eee339_android_proje.ui.classroom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.CaseScenario
import com.example.eee339_android_proje.data.entity.Classroom

class ClassroomDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val classroomDao = AppDatabase.getDatabase(application).classroomDao()
    private val caseScenarioDao = AppDatabase.getDatabase(application).caseScenarioDao()
    
    private val _classroomId = MutableLiveData<Long>()
    
    private var _classroom: LiveData<Classroom?>? = null
    val classroom: LiveData<Classroom?>
        get() = _classroom ?: MutableLiveData(null)
    
    private var _cases: LiveData<List<CaseScenario>>? = null
    val cases: LiveData<List<CaseScenario>>
        get() = _cases ?: MutableLiveData(emptyList())
    
    fun setClassroomId(classroomId: Long) {
        _classroomId.value = classroomId
        _classroom = classroomDao.getClassroomByIdLive(classroomId)
        _cases = caseScenarioDao.getCasesByClassroom(classroomId)
    }
}
