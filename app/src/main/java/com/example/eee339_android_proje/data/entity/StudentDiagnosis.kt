package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Öğrenci Tanısı Entity - Öğrencilerin vakalara koydukları tanılar ve öğretmen puanlaması
 */
@Entity(
    tableName = "student_diagnoses",
    foreignKeys = [
        ForeignKey(
            entity = CaseScenario::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["gradedByTeacherId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["caseId"]),
        Index(value = ["studentId"]),
        Index(value = ["gradedByTeacherId"])
    ]
)
data class StudentDiagnosis(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caseId: Long,
    val studentId: Long,
    val diagnosis: String,
    val explanation: String,
    val submittedAt: Long = System.currentTimeMillis(),
    val isCorrect: Boolean? = null,
    val score: Int? = null,
    val teacherFeedback: String? = null,
    val gradedByTeacherId: Long? = null,
    val gradedAt: Long? = null
)


