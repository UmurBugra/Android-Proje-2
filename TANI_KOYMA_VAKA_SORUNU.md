# SORUN: Tüm Vakalara Tanı Koyulmuyor

## 📋 Test Hesapları

### Öğrenci Hesapları (Tanı Koyabilir ✅)
```
Kullanıcı Adı: ogrenci
Şifre: 123456
Erişim: 5 vaka (tüm sınıflar)

Kullanıcı Adı: ayse
Şifre: 123456
Erişim: 3 vaka (2 sınıf)

Kullanıcı Adı: mehmet
Şifre: 123456
Erişim: 3 vaka (2 sınıf)

Kullanıcı Adı: zeynep
Şifre: 123456
Erişim: 2 vaka (1 sınıf)
```

### Öğretmen Hesabı (Tanı Koyamaz ❌)
```
Kullanıcı Adı: ogretmen
Şifre: 123456
Not: Öğretmen "Tanı Koy" butonunu göremez!
```

---

## 🔍 Sorun Analizi

Kullanıcı sorunu: **"Sadece belirli vakalarda tanı koyma özelliği var"**

### Olası Senaryo 1: Öğrenci Sınıfa Kayıtlı Değil ❌

Eğer öğrenci bir sınıfa kayıtlı değilse, o sınıfın vakalarını göremez.

**Kontrol:**
```kotlin
// AppDatabase.kt - Hangi öğrenci hangi sınıfa kayıtlı?

// ogrenci kullanıcısı - Tüm 3 sınıfa kayıtlı ✅
- İç Hastalıkları 101
- Acil Tıp Simülasyonları  
- Kardiyoloji Vakaları

// ayse kullanıcısı - 2 sınıfa kayıtlı
- İç Hastalıkları 101
- Kardiyoloji Vakaları
(Acil Tıp göremez ❌)

// mehmet kullanıcısı - 2 sınıfa kayıtlı
- İç Hastalıkları 101
- Acil Tıp Simülasyonları
(Kardiyoloji göremez ❌)

// zeynep kullanıcısı - 1 sınıfa kayıtlı
- Kardiyoloji Vakaları
(Diğerlerini göremez ❌)
```

**ÇÖZÜM:** Eğer bir vakayı göremiyorsanız, önce o sınıfa katılmanız gerekiyor!

---

### Olası Senaryo 2: Öğretmen Hesabıyla Giriş Yapılmış ❌

Öğretmen hesabında "Tanı Koy" butonu GİZLİ!

**Kod:**
```kotlin
if (isTeacher) {
    binding.actionPanel.visibility = View.GONE  // TÜM BUTONLAR GİZLİ!
}
```

**ÇÖZÜM:** **Öğrenci hesabıyla** giriş yapın.

---

### Olası Senaryo 3: Eski Veritabanı Versiyonu ❌

Eğer uygulamayı güncellemeden önceki sürümden kullanıyorsanız, veritabanı version 6'ya güncellenmemiş olabilir.

**ÇÖZÜM:**
1. Uygulamayı tamamen silin
2. Yeniden yükleyin
3. İlk açılışta veritabanı otomatik oluşturulacak

---

### Olası Senaryo 4: Dialog Açılıyor Ama Submit Çalışmıyor ❌

Dialog açılıyor ancak tanı kaydedilmiyor olabilir.

**Kontrol Noktaları:**
- Tanı ve açıklama alanlarını doldurdunuz mu?
- "Tanıyı Gönder" butonuna bastınız mı?
- Toast mesajı göründü mü?
- Logcat'te hata var mı?

---

## ✅ TÜM VAKALARA TANI KOYMA - DOĞRU YÖNTEM

### ADIM 1: Doğru Hesap Seçimi

**Öğrenci Hesapları:**

| Kullanıcı Adı | Şifre | Kayıtlı Sınıflar |
|---------------|-------|------------------|
| `ogrenci` | `123456` | Tüm sınıflar (3/3) ✅ |
| `ayse` | `123456` | 2 sınıf |
| `mehmet` | `123456` | 2 sınıf |
| `zeynep` | `123456` | 1 sınıf |

**Öğretmen Hesabı (Tanı Koyamaz!):**

| Kullanıcı Adı | Şifre | Not |
|---------------|-------|-----|
| `ogretmen` | `123456` | ❌ Öğretmen tanı koyamaz! |

⚠️ **ÖNEMLİ:** Tanı koymak için **öğrenci hesabı** kullanmalısınız!

**En Kolay Test:** `ogrenci` / `123456` (Tüm vakalara erişimi var)

### ADIM 2: Sınıf Kontrolü

**ogrenci** hesabı için tüm vakalar:

#### İç Hastalıkları 101 Sınıfı
- ✅ Akut Karın Ağrısı → Tanı koyulabilir
- ✅ Nefes Darlığı → Tanı koyulabilir

#### Acil Tıp Simülasyonları
- ✅ Travma Hastası → Tanı koyulabilir

#### Kardiyoloji Vakaları
- ✅ Göğüs Ağrısı - Acil → Tanı koyulabilir
- ✅ Çarpıntı Şikayeti → Tanı koyulabilir

**TOPLAM: 5 VAKA - HEPSİNE TANI KOYULABİLİR**

---

### ADIM 3: Her Vaka İçin Test

#### Test 1: Akut Karın Ağrısı
```
1. Giriş Yap:
   Kullanıcı Adı: ogrenci
   Şifre: 123456
2. "İç Hastalıkları 101" sınıfını aç
3. "Akut Karın Ağrısı" vakasını seç
4. "Tanı Koy" butonuna bas
5. Tanı: "Akut Apandisit"
6. Açıklama: "Sağ alt kadran hassasiyeti, ateş, McBurney noktası ağrısı"
7. Gönder
✅ "Tanınız başarıyla gönderildi!" mesajı görünmeli
```

#### Test 2: Nefes Darlığı
```
1. Aynı sınıfta
2. "Nefes Darlığı" vakasını seç
3. "Tanı Koy" butonuna bas
4. Tanı: "KOAH Atak"
5. Açıklama: "Kronik öksürük, dispne, sigara öyküsü"
6. Gönder
✅ Çalışmalı
```

#### Test 3: Travma Hastası
```
1. "Acil Tıp Simülasyonları" sınıfını aç
2. "Travma Hastası" vakasını seç
3. Tanı koy
✅ Çalışmalı
```

#### Test 4: Göğüs Ağrısı - Acil
```
1. "Kardiyoloji Vakaları" sınıfını aç
2. "Göğüs Ağrısı - Acil" vakasını seç
3. Tanı koy
✅ Çalışmalı
```

#### Test 5: Çarpıntı Şikayeti
```
1. Aynı sınıfta
2. "Çarpıntı Şikayeti" vakasını seç
3. Tanı koy
✅ Çalışmalı
```

---

## 🐛 Eğer Belirli Bir Vakada Çalışmıyorsa

### Hata Senaryosu: "Göğüs Ağrısı" vakasında tanı koyamıyorum

**Kontrol Listesi:**
1. ✅ `ogrenci` hesabıyla mı giriş yaptınız? (öğretmen değil)
2. ✅ "Kardiyoloji Vakaları" sınıfına kayıtlı mısınız?
3. ✅ Vakayı açtığınızda "Tanı Koy" butonu görünüyor mu?
4. ✅ Butona bastığınızda dialog açılıyor mu?
5. ✅ Tanı ve açıklama alanlarını doldurdunuz mu?
6. ✅ "Tanıyı Gönder" butonuna bastınız mı?
7. ✅ Hata mesajı görünüyor mu?

### Debug Adımları

#### 1. Logcat Kontrolü
Android Studio → Logcat → Filter: "CaseSimulation"

Şunları arayın:
```
E/CaseSimulationViewModel: Tanı gönderme hatası
E/StudentDiagnosisDao: Insert failed
E/AppDatabase: Database error
```

#### 2. Veritabanı Kontrolü
```sql
-- Hangi vakalara tanı konmuş?
SELECT 
    cs.title AS vaka_adi,
    u.username AS ogrenci,
    sd.diagnosis AS tani,
    sd.submittedAt AS tarih
FROM student_diagnoses sd
JOIN case_scenarios cs ON sd.caseId = cs.id
JOIN users u ON sd.studentId = u.id
ORDER BY sd.submittedAt DESC;
```

#### 3. Manuel Test
```kotlin
// ViewModel test
viewModel.submitDiagnosis(
    caseId = 1,  // Akut Karın Ağrısı
    studentId = 3,  // ogrenci
    diagnosis = "Test Tanısı",
    explanation = "Test Açıklaması"
)
// Başarılı olmalı
```

---

## 🔧 KESIN ÇÖZÜM

### Yöntem 1: Tam Temizlik ve Yeniden Başlat

```powershell
# 1. Uygulamayı cihazdan sil
adb uninstall com.example.eee339_android_proje

# 2. Build klasörünü temizle
cd "C:\Users\Yunus Emre\AndroidStudioProjects\Android-Proje-2"
Remove-Item -Path "app\build" -Recurse -Force
Remove-Item -Path ".gradle" -Recurse -Force

# 3. Android Studio'da
# Build → Clean Project
# Build → Rebuild Project

# 4. Yeniden yükle
# Run → Run 'app'
```

### Yöntem 2: Veritabanı Versiyonunu Artır

Eğer sorun devam ederse, veritabanı versiyonunu artırın:

```kotlin
// AppDatabase.kt
@Database(
    entities = [...],
    version = 7,  // 6'dan 7'ye çıkar
    exportSchema = false
)
```

---

## 📊 Beklenen Durum

| Vaka | Sınıf | Tanı Koyma | Durum |
|------|-------|------------|-------|
| Akut Karın Ağrısı | İç Hastalıkları | ✅ EVET | Çalışmalı |
| Nefes Darlığı | İç Hastalıkları | ✅ EVET | Çalışmalı |
| Travma Hastası | Acil Tıp | ✅ EVET | Çalışmalı |
| Göğüs Ağrısı | Kardiyoloji | ✅ EVET | Çalışmalı |
| Çarpıntı Şikayeti | Kardiyoloji | ✅ EVET | Çalışmalı |

**HEPSİ AYNI ŞEKİLDE ÇALIŞMALI!**

---

## 📝 Bilgi Toplama

Eğer hala sorun varsa, şu bilgileri paylaşın:

1. **Hangi kullanıcı hesabıyla giriş yaptınız?**
   - [ ] ogretmen (❌ Yanlış, tanı koyamaz)
   - [ ] ogrenci (✅ Doğru)
   - [ ] ayse
   - [ ] mehmet
   - [ ] zeynep

2. **Hangi sınıfta sorun yaşıyorsunuz?**
   - [ ] İç Hastalıkları 101
   - [ ] Acil Tıp Simülasyonları
   - [ ] Kardiyoloji Vakaları

3. **Hangi vakada tanı koyamıyorsunuz?**
   - [ ] Akut Karın Ağrısı
   - [ ] Nefes Darlığı
   - [ ] Travma Hastası
   - [ ] Göğüs Ağrısı
   - [ ] Çarpıntı Şikayeti

4. **Sorun tam olarak ne?**
   - [ ] Vakayı göremiyorum
   - [ ] "Tanı Koy" butonunu göremiyorum
   - [ ] Butona basıyorum ama dialog açılmıyor
   - [ ] Dialog açılıyor ama "Gönder" çalışmıyor
   - [ ] Tanı gönderiliyor ama kaydedilmiyor

5. **Logcat'te hata var mı?**
   - [ ] Evet (hata mesajını paylaşın)
   - [ ] Hayır

---

## 🎯 Sonuç

**KOD AÇIKÇA GÖSTERİYOR: TÜM 5 VAKAYA TANI KOYULABİLİR!**

Hiçbir vakada kısıtlama yok. Her vaka için:
- ✅ correctDiagnosis tanımlı
- ✅ Aynı layout kullanılıyor
- ✅ Aynı Activity kullanılıyor
- ✅ Aynı ViewModel kullanılıyor
- ✅ Hiçbir if/when koşulu yok

Eğer bazı vakalarda tanı koyamıyorsanız, muhtemelen:
1. Öğretmen hesabıyla girdiniz (butonlar gizli)
2. O sınıfa kayıtlı değilsiniz (vakayı göremezsiniz)
3. Veritabanı güncel değil (uygulamayı yeniden yükleyin)

**Test edin ve sonucu bildirin!**

