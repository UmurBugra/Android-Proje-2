# 🎓 Sınıf Yönetimi Özellikleri

## ✅ Eklenen Özellikler

### 1. **Öğretmen Özellikleri**
- ✅ Yeni sınıf oluşturma
- ✅ Sınıfa benzersiz katılım kodu atama
- ✅ Sınıf adı, açıklama ve katılım kodu belirleme
- ✅ Oluşturulan tüm sınıfları görüntüleme

### 2. **Öğrenci Özellikleri**
- ✅ Katılım kodu ile sınıfa katılma
- ✅ Kayıtlı olunan sınıfları görüntüleme
- ✅ Sınıfa çift kayıt kontrolü (aynı sınıfa iki kez katılma engellenir)

### 3. **Veritabanı Yapısı**
- ✅ **ClassroomEnrollment** entity'si (öğrenci-sınıf ilişkisi)
- ✅ **ClassroomEnrollmentDao** (kayıt işlemleri için)
- ✅ Classroom entity'sine **joinCode** alanı eklendi
- ✅ Veritabanı versiyonu 2'den 3'e yükseltildi

---

## 📋 Kullanım Senaryoları

### Öğretmen Olarak Sınıf Oluşturma:

1. Öğretmen hesabı ile giriş yapın (`ogretmen` / `123456`)
2. Ana ekranda "Yeni Sınıf" butonuna tıklayın
3. Sınıf bilgilerini doldurun:
   - **Sınıf Adı:** Örn: "Anatomi 101"
   - **Katılım Kodu:** Örn: "ANAT101" (büyük harflerle)
   - **Açıklama:** İsteğe bağlı
4. "Sınıf Oluştur" butonuna tıklayın
5. Sınıf otomatik olarak öğretmenin panelinde görünür

### Öğrenci Olarak Sınıfa Katılma:

1. Öğrenci hesabı ile giriş yapın (`ogrenci` / `123456`)
2. Ana ekranda "Katıl" butonuna tıklayın
3. Öğretmenden aldığınız katılım kodunu girin (örn: "IC101")
4. "Katıl" butonuna tıklayın
5. Başarılı mesajı aldıktan sonra sınıf listenizde görünür

---

## 🎯 Demo Sınıflar ve Katılım Kodları

Veritabanında otomatik oluşturulan demo sınıflar:

| Sınıf Adı | Katılım Kodu | Açıklama |
|-----------|--------------|----------|
| İç Hastalıkları 101 | **IC101** | Temel iç hastalıkları vaka çalışmaları |
| Acil Tıp Simülasyonları | **ACIL22** | Acil servis vaka senaryoları |
| Kardiyoloji Vakaları | **KARD33** | Kalp hastalıkları vaka analizleri |

### Öğrenciler için Test:
1. `ogrenci` hesabı ile giriş yapın
2. "Katıl" butonuna tıklayın
3. Katılım kodunu girin: **IC101** veya **ACIL22** veya **KARD33**
4. Sınıfa katılın ve içeriği görüntüleyin

---

## 🔧 Teknik Detaylar

### Yeni Dosyalar:
1. **ClassroomEnrollment.kt** - Öğrenci-sınıf ilişki entity'si
2. **ClassroomEnrollmentDao.kt** - Kayıt işlemleri DAO
3. **dialog_create_classroom.xml** - Sınıf oluşturma dialog layout
4. **dialog_join_classroom.xml** - Sınıfa katılma dialog layout

### Güncellenen Dosyalar:
1. **Classroom.kt** - joinCode alanı eklendi
2. **ClassroomDao.kt** - joinCode ile arama metodu eklendi
3. **AppDatabase.kt** - Version 3, yeni entity eklendi
4. **TeacherDashboardViewModel.kt** - createClassroom metodu
5. **StudentDashboardViewModel.kt** - joinClassroom metodu, kayıtlı sınıf listeleme
6. **TeacherDashboardActivity.kt** - Sınıf oluşturma dialog
7. **StudentDashboardActivity.kt** - Sınıfa katılma dialog
8. **activity_student_dashboard.xml** - "Katıl" butonu eklendi
9. **strings.xml** - İlgili string kaynakları

### Veritabanı Değişiklikleri:
- **Versiyon:** 2 → 3
- **Yeni Tablo:** classroom_enrollments
- **Güncellenen Tablo:** classrooms (joinCode alanı)
- **Migration:** fallbackToDestructiveMigration (otomatik yeniden oluşturma)

---

## ⚠️ Önemli Notlar

### Veritabanını Güncelleme:
Veritabanı versiyonu değiştiği için uygulamayı temizlemeniz gerekir:

```powershell
# Terminal/CMD ile:
adb shell pm clear com.example.eee339_android_proje
```

veya

1. Telefon/Emülatör → Ayarlar
2. Uygulamalar → C-learn
3. Depolama → Verileri Temizle
4. Uygulamayı tekrar başlatın

### Katılım Kodu Kuralları:
- Katılım kodları **büyük harfe** çevrilir (otomatik)
- Her sınıfın katılım kodu **benzersiz** olmalıdır
- Boş bırakılamaz
- Önerilen format: `DERS###` (örn: IC101, ANAT202)

### Güvenlik Notları:
- Öğrenciler yalnızca kayıtlı oldukları sınıfları görebilir
- Öğretmenler yalnızca kendi oluşturdukları sınıfları yönetebilir
- Aynı öğrenci bir sınıfa yalnızca bir kez kayıt olabilir

---

## 🧪 Test Senaryoları

### Senaryo 1: Yeni Sınıf Oluşturma
1. Öğretmen hesabı ile giriş yap
2. "Yeni Sınıf" tıkla
3. Bilgileri doldur ve oluştur
4. Sınıfın listede göründüğünü doğrula

### Senaryo 2: Sınıfa Katılma
1. Öğrenci hesabı ile giriş yap
2. "Katıl" butonuna tıkla
3. Demo kodlardan birini gir (IC101)
4. Katıldığını doğrula

### Senaryo 3: Çift Kayıt Engelleme
1. Öğrenci ile bir sınıfa katıl
2. Aynı sınıfa tekrar katılmayı dene
3. "Zaten kayıtlısınız" hata mesajını gör

### Senaryo 4: Geçersiz Kod
1. Öğrenci ile "Katıl" tıkla
2. Var olmayan bir kod gir (TEST999)
3. "Geçersiz katılım kodu" hata mesajını gör

---

## 📱 Ekran Görüntüleri İçin Öneriler

1. **Öğretmen Paneli** - Sınıf listesi ve "Yeni Sınıf" butonu
2. **Sınıf Oluşturma Dialog** - Form alanları dolu halde
3. **Öğrenci Paneli** - Kayıtlı sınıflar ve "Katıl" butonu
4. **Katılma Dialog** - Katılım kodu girişi
5. **Başarı Mesajı** - Toast bildirimleri

---

## 🚀 Gelecek Özellikler

- [ ] Sınıftan ayrılma
- [ ] Sınıf silme (öğretmen için)
- [ ] Sınıf düzenleme
- [ ] Öğrenci listesi görüntüleme
- [ ] Sınıf istatistikleri
- [ ] Toplu öğrenci ekleme
- [ ] QR kod ile katılım

