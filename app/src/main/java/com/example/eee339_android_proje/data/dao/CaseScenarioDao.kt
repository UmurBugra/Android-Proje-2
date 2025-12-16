package com.example.eee339_android_proje.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eee339_android_proje.data.entity.CaseScenario

@Dao
interface CaseScenarioDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(caseScenario: CaseScenario): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<CaseScenario>)
    
    @Query("SELECT * FROM case_scenarios WHERE classroomId = :classroomId ORDER BY title ASC")
    fun getCasesByClassroom(classroomId: Long): LiveData<List<CaseScenario>>
    
    @Query("SELECT * FROM case_scenarios WHERE id = :caseId")
    suspend fun getCaseById(caseId: Long): CaseScenario?
    
    @Query("SELECT * FROM case_scenarios WHERE id = :caseId")
    fun getCaseByIdLive(caseId: Long): LiveData<CaseScenario?>
    
    @Query("SELECT COUNT(*) FROM case_scenarios WHERE classroomId = :classroomId")
    suspend fun getCaseCountByClassroom(classroomId: Long): Int
}
