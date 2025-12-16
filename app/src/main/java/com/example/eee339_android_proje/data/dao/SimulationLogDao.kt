package com.example.eee339_android_proje.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eee339_android_proje.data.entity.SimulationLog

@Dao
interface SimulationLogDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SimulationLog): Long
    
    @Query("SELECT * FROM simulation_logs WHERE caseId = :caseId AND studentId = :studentId ORDER BY timestamp DESC")
    fun getLogsByCase(caseId: Long, studentId: Long): LiveData<List<SimulationLog>>
    
    @Query("SELECT * FROM simulation_logs WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getLogsByStudent(studentId: Long): LiveData<List<SimulationLog>>
    
    @Query("SELECT * FROM simulation_logs WHERE caseId = :caseId AND studentId = :studentId ORDER BY timestamp DESC")
    suspend fun getLogsByCaseSync(caseId: Long, studentId: Long): List<SimulationLog>
}
