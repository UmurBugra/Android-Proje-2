package com.example.eee339_android_proje.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kullanıcı Entity - Öğrenci ve Öğretmen bilgilerini tutar
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val role: String  // "STUDENT" veya "TEACHER"
) {
    companion object {
        const val ROLE_STUDENT = "STUDENT"
        const val ROLE_TEACHER = "TEACHER"
    }
}
