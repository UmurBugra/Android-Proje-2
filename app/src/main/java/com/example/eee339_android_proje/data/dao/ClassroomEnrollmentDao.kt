package com.example.eee339_android_proje.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eee339_android_proje.data.entity.Classroom
import com.example.eee339_android_proje.data.entity.ClassroomEnrollment

@Dao
interface ClassroomEnrollmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enroll(enrollment: ClassroomEnrollment): Long

    @Query("DELETE FROM classroom_enrollments WHERE classroomId = :classroomId AND studentId = :studentId")
    suspend fun unenroll(classroomId: Long, studentId: Long)

    @Query("SELECT * FROM classrooms WHERE id IN (SELECT classroomId FROM classroom_enrollments WHERE studentId = :studentId) ORDER BY className ASC")
    fun getEnrolledClassrooms(studentId: Long): LiveData<List<Classroom>>

    @Query("SELECT COUNT(*) FROM classroom_enrollments WHERE classroomId = :classroomId")
    suspend fun getEnrollmentCount(classroomId: Long): Int

    @Query("SELECT COUNT(*) FROM classroom_enrollments WHERE classroomId = :classroomId AND studentId = :studentId")
    suspend fun isEnrolled(classroomId: Long, studentId: Long): Int

    @Query("SELECT * FROM classroom_enrollments WHERE classroomId = :classroomId")
    fun getEnrollmentsByClassroom(classroomId: Long): LiveData<List<ClassroomEnrollment>>
}

