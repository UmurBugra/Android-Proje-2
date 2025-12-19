package com.example.eee339_android_proje.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.eee339_android_proje.data.dao.AnnouncementDao
import com.example.eee339_android_proje.data.dao.CaseScenarioDao
import com.example.eee339_android_proje.data.dao.ClassroomDao
import com.example.eee339_android_proje.data.dao.ClassroomEnrollmentDao
import com.example.eee339_android_proje.data.dao.SimulationLogDao
import com.example.eee339_android_proje.data.dao.StudentDiagnosisDao
import com.example.eee339_android_proje.data.dao.UserDao
import com.example.eee339_android_proje.data.entity.Announcement
import com.example.eee339_android_proje.data.entity.CaseScenario
import com.example.eee339_android_proje.data.entity.Classroom
import com.example.eee339_android_proje.data.entity.ClassroomEnrollment
import com.example.eee339_android_proje.data.entity.SimulationLog
import com.example.eee339_android_proje.data.entity.StudentDiagnosis
import com.example.eee339_android_proje.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Classroom::class, CaseScenario::class, SimulationLog::class, ClassroomEnrollment::class, Announcement::class, StudentDiagnosis::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun classroomDao(): ClassroomDao
    abstract fun caseScenarioDao(): CaseScenarioDao
    abstract fun simulationLogDao(): SimulationLogDao
    abstract fun classroomEnrollmentDao(): ClassroomEnrollmentDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun studentDiagnosisDao(): StudentDiagnosisDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clearn_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(SeedDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * İlk çalıştırmada demo verileri ekler
     */
    private class SeedDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }
        
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Her açılışta kontrol et, eğer kullanıcı yoksa demo verileri ekle
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val userCount = database.userDao().getUserCount()
                    if (userCount == 0) {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val userDao = database.userDao()
            val classroomDao = database.classroomDao()
            val caseScenarioDao = database.caseScenarioDao()
            
            // Demo Kullanıcılar
            // Öğretmen Hesapları
            val teacherId = userDao.insert(
                User(
                    username = "ogretmen",
                    password = "123456",
                    role = User.ROLE_TEACHER
                )
            )

            userDao.insert(
                User(
                    username = "drahmet",
                    password = "123456",
                    role = User.ROLE_TEACHER
                )
            )
            
            // Öğrenci Hesapları
            val studentId1 = userDao.insert(
                User(
                    username = "ogrenci",
                    password = "123456",
                    role = User.ROLE_STUDENT
                )
            )

            val studentId2 = userDao.insert(
                User(
                    username = "ayse",
                    password = "123456",
                    role = User.ROLE_STUDENT
                )
            )

            val studentId3 = userDao.insert(
                User(
                    username = "mehmet",
                    password = "123456",
                    role = User.ROLE_STUDENT
                )
            )

            val studentId4 = userDao.insert(
                User(
                    username = "zeynep",
                    password = "123456",
                    role = User.ROLE_STUDENT
                )
            )
            
            // Demo Sınıflar
            val classroom1Id = classroomDao.insert(
                Classroom(
                    className = "İç Hastalıkları 101",
                    description = "Temel iç hastalıkları vaka çalışmaları",
                    teacherId = teacherId,
                    joinCode = "IC101"
                )
            )
            
            val classroom2Id = classroomDao.insert(
                Classroom(
                    className = "Acil Tıp Simülasyonları",
                    description = "Acil servis vaka senaryoları",
                    teacherId = teacherId,
                    joinCode = "ACIL22"
                )
            )
            
            val classroom3Id = classroomDao.insert(
                Classroom(
                    className = "Kardiyoloji Vakaları",
                    description = "Kalp hastalıkları vaka analizleri",
                    teacherId = teacherId,
                    joinCode = "KARD33"
                )
            )
            
            // Öğrencileri sınıflara kaydet
            val enrollmentDao = database.classroomEnrollmentDao()

            // Öğrenci 1 (ogrenci) - Tüm sınıflara kayıtlı
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom1Id, studentId = studentId1))
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom2Id, studentId = studentId1))
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom3Id, studentId = studentId1))

            // Öğrenci 2 (ayse) - İç Hastalıkları ve Kardiyoloji
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom1Id, studentId = studentId2))
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom3Id, studentId = studentId2))

            // Öğrenci 3 (mehmet) - İç Hastalıkları ve Acil Tıp
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom1Id, studentId = studentId3))
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom2Id, studentId = studentId3))

            // Öğrenci 4 (zeynep) - Sadece Kardiyoloji
            enrollmentDao.enroll(ClassroomEnrollment(classroomId = classroom3Id, studentId = studentId4))

            // Demo Vaka Senaryoları
            caseScenarioDao.insertAll(listOf(
                CaseScenario(
                    title = "Akut Karın Ağrısı",
                    patientInfo = "45 yaşında erkek hasta. 2 gündür devam eden karın ağrısı, bulantı ve kusma şikayetiyle başvurdu. Daha önce benzer şikayeti olmamış.",
                    vitalSigns = """{"ates": "38.2°C", "nabiz": "92/dk", "tansiyon": "130/85 mmHg", "solunum": "18/dk"}""",
                    correctDiagnosis = "Akut Apandisit",
                    classroomId = classroom1Id
                ),
                CaseScenario(
                    title = "Nefes Darlığı",
                    patientInfo = "62 yaşında kadın hasta. Son 1 haftadır artan nefes darlığı, öksürük ve göğüs ağrısı. KOAH öyküsü mevcut, 40 paket/yıl sigara kullanımı.",
                    vitalSigns = """{"ates": "37.8°C", "nabiz": "110/dk", "tansiyon": "145/90 mmHg", "solunum": "28/dk", "spo2": "%88"}""",
                    correctDiagnosis = "KOAH Atak",
                    classroomId = classroom1Id
                ),
                CaseScenario(
                    title = "Travma Hastası",
                    patientInfo = "28 yaşında erkek hasta. Trafik kazası sonrası acil servise getiriliyor. Bilinç açık, oryante. Sol bacakta ağrı ve şişlik mevcut.",
                    vitalSigns = """{"ates": "36.8°C", "nabiz": "105/dk", "tansiyon": "100/70 mmHg", "solunum": "22/dk"}""",
                    correctDiagnosis = "Sol Femur Kırığı",
                    classroomId = classroom2Id
                ),
                CaseScenario(
                    title = "Göğüs Ağrısı - Acil",
                    patientInfo = "55 yaşında erkek hasta. 1 saat önce başlayan göğüs ağrısı, sol kola yayılıyor. Terleme ve bulantı eşlik ediyor. Hipertansiyon ve diyabet öyküsü var.",
                    vitalSigns = """{"ates": "37.0°C", "nabiz": "88/dk", "tansiyon": "160/100 mmHg", "solunum": "20/dk"}""",
                    correctDiagnosis = "Akut Miyokard Enfarktüsü",
                    classroomId = classroom3Id
                ),
                CaseScenario(
                    title = "Çarpıntı Şikayeti",
                    patientInfo = "35 yaşında kadın hasta. Son 2 aydır aralıklı çarpıntı, baş dönmesi hissi. Tiroid hastalığı öyküsü yok.",
                    vitalSigns = """{"ates": "36.6°C", "nabiz": "145/dk (düzensiz)", "tansiyon": "120/80 mmHg", "solunum": "16/dk"}""",
                    correctDiagnosis = "Paroksismal Atriyal Fibrilasyon",
                    classroomId = classroom3Id
                )
            ))
        }
    }
}
