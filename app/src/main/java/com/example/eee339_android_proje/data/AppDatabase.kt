package com.example.eee339_android_proje.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.eee339_android_proje.data.dao.CaseScenarioDao
import com.example.eee339_android_proje.data.dao.ClassroomDao
import com.example.eee339_android_proje.data.dao.SimulationLogDao
import com.example.eee339_android_proje.data.dao.UserDao
import com.example.eee339_android_proje.data.entity.CaseScenario
import com.example.eee339_android_proje.data.entity.Classroom
import com.example.eee339_android_proje.data.entity.SimulationLog
import com.example.eee339_android_proje.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Classroom::class, CaseScenario::class, SimulationLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun classroomDao(): ClassroomDao
    abstract fun caseScenarioDao(): CaseScenarioDao
    abstract fun simulationLogDao(): SimulationLogDao
    
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
        
        private suspend fun populateDatabase(database: AppDatabase) {
            val userDao = database.userDao()
            val classroomDao = database.classroomDao()
            val caseScenarioDao = database.caseScenarioDao()
            
            // Demo Kullanıcılar
            val teacherId = userDao.insert(
                User(
                    username = "teacher",
                    password = "teacher123",
                    role = User.ROLE_TEACHER
                )
            )
            
            val studentId = userDao.insert(
                User(
                    username = "student",
                    password = "student123",
                    role = User.ROLE_STUDENT
                )
            )
            
            // Demo Sınıflar
            val classroom1Id = classroomDao.insert(
                Classroom(
                    className = "İç Hastalıkları 101",
                    description = "Temel iç hastalıkları vaka çalışmaları",
                    teacherId = teacherId
                )
            )
            
            val classroom2Id = classroomDao.insert(
                Classroom(
                    className = "Acil Tıp Simülasyonları",
                    description = "Acil servis vaka senaryoları",
                    teacherId = teacherId
                )
            )
            
            val classroom3Id = classroomDao.insert(
                Classroom(
                    className = "Kardiyoloji Vakaları",
                    description = "Kalp hastalıkları vaka analizleri",
                    teacherId = teacherId
                )
            )
            
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
