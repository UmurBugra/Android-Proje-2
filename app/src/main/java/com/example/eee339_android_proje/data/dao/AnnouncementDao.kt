package com.example.eee339_android_proje.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eee339_android_proje.data.entity.Announcement

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: Announcement): Long

    @Query("SELECT * FROM announcements WHERE classroomId = :classroomId ORDER BY createdAt DESC")
    fun getAnnouncementsByClassroom(classroomId: Long): LiveData<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE classroomId IN (:classroomIds) ORDER BY createdAt DESC LIMIT 5")
    fun getRecentAnnouncementsByClassrooms(classroomIds: List<Long>): LiveData<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE teacherId = :teacherId ORDER BY createdAt DESC")
    fun getAnnouncementsByTeacher(teacherId: Long): LiveData<List<Announcement>>

    @Query("DELETE FROM announcements WHERE id = :announcementId")
    suspend fun deleteAnnouncement(announcementId: Long)
}

