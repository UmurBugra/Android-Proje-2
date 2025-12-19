package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Duyuru Entity - Öğretmenlerin sınıflara yaptığı duyurular
 */
@Entity(
    tableName = "announcements",
    foreignKeys = [
        ForeignKey(
            entity = Classroom::class,
            parentColumns = ["id"],
            childColumns = ["classroomId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["classroomId"]),
        Index(value = ["teacherId"])
    ]
)
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val classroomId: Long,
    val teacherId: Long,
    val title: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)


