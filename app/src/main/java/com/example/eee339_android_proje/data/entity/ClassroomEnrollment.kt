package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sınıf-Öğrenci İlişkisi Entity - Öğrencilerin hangi sınıflara kayıtlı olduğunu tutar
 */
@Entity(
    tableName = "classroom_enrollments",
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
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["classroomId"]),
        Index(value = ["studentId"]),
        Index(value = ["classroomId", "studentId"], unique = true)
    ]
)
data class ClassroomEnrollment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val classroomId: Long,
    val studentId: Long,
    val enrollmentDate: Long = System.currentTimeMillis()
)

