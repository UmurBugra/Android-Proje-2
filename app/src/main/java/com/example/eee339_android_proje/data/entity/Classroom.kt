package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sınıf Entity - Derslikleri temsil eder
 */
@Entity(
    tableName = "classrooms",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["teacherId"])]
)
data class Classroom(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val className: String,
    val description: String = "",
    val teacherId: Long
)
