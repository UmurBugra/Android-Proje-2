package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Vaka Senaryosu Entity - Hasta vakaları
 */
@Entity(
    tableName = "case_scenarios",
    foreignKeys = [
        ForeignKey(
            entity = Classroom::class,
            parentColumns = ["id"],
            childColumns = ["classroomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["classroomId"])]
)
data class CaseScenario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val patientInfo: String,        // Hasta bilgileri (yaş, cinsiyet, şikayet)
    val vitalSigns: String,         // JSON formatında vital bulgular
    val correctDiagnosis: String,   // Doğru tanı
    val classroomId: Long
)
