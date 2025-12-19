package com.example.eee339_android_proje.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eee339_android_proje.data.entity.StudentDiagnosis

@Dao
interface StudentDiagnosisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diagnosis: StudentDiagnosis): Long

    @Update
    suspend fun update(diagnosis: StudentDiagnosis)

    // Öğrencinin bir vakaya koyduğu tanı
    @Query("SELECT * FROM student_diagnoses WHERE caseId = :caseId AND studentId = :studentId ORDER BY submittedAt DESC LIMIT 1")
    suspend fun getDiagnosisByStudentAndCase(caseId: Long, studentId: Long): StudentDiagnosis?

    @Query("SELECT * FROM student_diagnoses WHERE caseId = :caseId AND studentId = :studentId ORDER BY submittedAt DESC LIMIT 1")
    fun getDiagnosisByStudentAndCaseLive(caseId: Long, studentId: Long): LiveData<StudentDiagnosis?>

    // Bir vakadaki tüm öğrenci tanıları (öğretmen için)
    @Query("SELECT * FROM student_diagnoses WHERE caseId = :caseId ORDER BY submittedAt DESC")
    fun getDiagnosesByCase(caseId: Long): LiveData<List<StudentDiagnosis>>

    // Bir sınıftaki tüm öğrenci tanıları (öğretmen için)
    @Query("""
        SELECT sd.* FROM student_diagnoses sd
        INNER JOIN case_scenarios cs ON sd.caseId = cs.id
        WHERE cs.classroomId = :classroomId
        ORDER BY sd.submittedAt DESC
    """)
    fun getDiagnosesByClassroom(classroomId: Long): LiveData<List<StudentDiagnosis>>

    // Puanlanmamış tanılar
    @Query("SELECT * FROM student_diagnoses WHERE score IS NULL ORDER BY submittedAt ASC")
    fun getUngradedDiagnoses(): LiveData<List<StudentDiagnosis>>

    // Öğretmenin sınıfındaki puanlanmamış tanılar
    @Query("""
        SELECT sd.* FROM student_diagnoses sd
        INNER JOIN case_scenarios cs ON sd.caseId = cs.id
        WHERE cs.classroomId = :classroomId AND sd.score IS NULL
        ORDER BY sd.submittedAt ASC
    """)
    fun getUngradedDiagnosesByClassroom(classroomId: Long): LiveData<List<StudentDiagnosis>>

    // Öğrencinin tüm tanıları
    @Query("SELECT * FROM student_diagnoses WHERE studentId = :studentId ORDER BY submittedAt DESC")
    fun getDiagnosesByStudent(studentId: Long): LiveData<List<StudentDiagnosis>>
}

