package com.example.eee339_android_proje.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eee339_android_proje.data.entity.Classroom

@Dao
interface ClassroomDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(classroom: Classroom): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(classrooms: List<Classroom>)
    
    @Query("SELECT * FROM classrooms ORDER BY className ASC")
    fun getAllClassrooms(): LiveData<List<Classroom>>
    
    @Query("SELECT * FROM classrooms WHERE teacherId = :teacherId ORDER BY className ASC")
    fun getClassroomsByTeacher(teacherId: Long): LiveData<List<Classroom>>
    
    @Query("SELECT * FROM classrooms WHERE id = :classroomId")
    suspend fun getClassroomById(classroomId: Long): Classroom?
    
    @Query("SELECT * FROM classrooms WHERE id = :classroomId")
    fun getClassroomByIdLive(classroomId: Long): LiveData<Classroom?>
}
