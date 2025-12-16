package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Simülasyon Log Entity - Öğrenci işlemlerinin kaydı
 */
@Entity(
    tableName = "simulation_logs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CaseScenario::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["caseId"])
    ]
)
data class SimulationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val caseId: Long,
    val actionName: String,         // İşlem adı (Tahlil İste, Muayene Et, vb.)
    val feedbackMessage: String,    // Sistem geri bildirimi
    val timestamp: Long = System.currentTimeMillis()
)
