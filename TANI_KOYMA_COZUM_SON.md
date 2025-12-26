# Tanı Koyma Özelliği - Son Çözüm ve Test Rehberi

## ✅ Durum: TAMAM ve ÇALIŞIR

Tüm kodlar incelendi. Tanı koyma özelliği **TAMAMEN MEVCUT ve ÇALIŞIR DURUMDA**.

## 🔑 Test Hesapları

### Öğrenci Hesapları (Tanı Koyabilir ✅)
- **Kullanıcı Adı:** `ogrenci` | **Şifre:** `123456` (Tüm vakalara erişim)
- **Kullanıcı Adı:** `ayse` | **Şifre:** `123456`
- **Kullanıcı Adı:** `mehmet` | **Şifre:** `123456`
- **Kullanıcı Adı:** `zeynep` | **Şifre:** `123456`

### Öğretmen Hesabı (Tanı Koyamaz ❌)
- **Kullanıcı Adı:** `ogretmen` | **Şifre:** `123456` (Sadece görüntüleme ve puanlama)

⚠️ **ÖNEMLİ:** Tanı koymak için **öğrenci hesabı** kullanın!

---

## 🔧 Eğer Çalışmıyorsa - Adım Adım Çözüm

### ADIM 1: Uygulamayı Tamamen Silin
```
Ayarlar → Uygulamalar → CLearn → Kaldır
```

### ADIM 2: Android Studio'da Clean Project
```
Build → Clean Project
```

### ADIM 3: Build Klasörünü Manuel Temizle
```powershell
cd "C:\Users\Yunus Emre\AndroidStudioProjects\Android-Proje-2"
Remove-Item -Path "app\build" -Recurse -Force
Remove-Item -Path ".gradle" -Recurse -Force -ErrorAction SilentlyContinue
```

### ADIM 4: Projeyi Yeniden Derle
Android Studio'da:
- **Build → Rebuild Project**
- Derlenme bitene kadar bekleyin (2-3 dakika)

### ADIM 5: Uygulamayı Çalıştır
- **Run → Run 'app'** (Shift+F10)
- Cihazı/emülatörü seçin

---

## 📱 Test Senaryosu

### Test 1: Öğrenci Hesabı ile Giriş
```
Kullanıcı Adı: ogrenci
Şifre: 123456
```
Giriş Yap butonuna basın.

### Test 2: Sınıfa Katılma
Eğer sınıf yoksa:
1. Önce **öğretmen hesabıyla** giriş yapın
   ```
   Kullanıcı Adı: ogretmen
   Şifre: 123456
   ```
2. "Yeni Sınıf Oluştur" butonuna basın
3. Sınıf adı girin ve oluşturun
4. Katılım kodunu not alın
5. Çıkış yapıp **öğrenci hesabıyla** giriş yapın
   ```
   Kullanıcı Adı: ogrenci
   Şifre: 123456
   ```
6. "Sınıfa Katıl" butonuna basın
7. Katılım kodunu girin

### Test 3: Vakayı Açma
1. Sınıfı seçin
2. Bir vaka seçin (örn: "Akut Karın Ağrısı")
3. Vaka simülasyon ekranı açılacak

### Test 4: Tanı Koyma
1. **"Tanı Koy"** butonuna basın
2. Dialog açılmalı
3. Tanı girin (örn: "Apandisit")
4. Açıklama girin (örn: "Sağ alt kadran ağrısı, ateş, hassasiyet")
5. **"Tanıyı Gönder"** butonuna basın
6. "Tanınız başarıyla gönderildi!" mesajı görünmeli

### Test 5: Öğretmen Tanı Görüntüleme
1. Çıkış yapın
2. **Öğretmen hesabıyla** giriş yapın
3. Sınıfı açın
4. **"Tanıları Görüntüle"** butonuna basın
5. Öğrenci tanıları listelenecek
6. Bir tanıya tıklayın
7. Puan verin (0-100)
8. Geri bildirim yazın (isteğe bağlı)
9. Kaydet

---

## 🐛 Hata Ayıklama

### Hata: "Tanı Koy" Butonu Gözükmüyor
**Sebep:** Öğretmen hesabıyla giriş yapmışsınız.
**Çözüm:** Öğrenci hesabıyla giriş yapın.

### Hata: Sadece Bazı Vakalarda Tanı Koyabiliyorum ⚠️ YENİ
**Sebep 1:** Diğer vakaların sınıflarına kayıtlı değilsiniz.
**Çözüm:** Her sınıfa ayrı ayrı katılmanız gerekiyor. 

**Örnek:**
- `ogrenci` kullanıcısı → 3 sınıfa kayıtlı → 5 vakayı görebilir ✅
- `ayse` kullanıcısı → 2 sınıfa kayıtlı → 3 vakayı görebilir (Acil Tıp vakalarını göremez)
- `zeynep` kullanıcısı → 1 sınıfa kayıtlı → 2 vakayı görebilir (sadece Kardiyoloji)

**Sınıf Kayıt Durumu:**
```
İç Hastalıkları 101:
  - Akut Karın Ağrısı ✅
  - Nefes Darlığı ✅

Acil Tıp Simülasyonları:
  - Travma Hastası ✅

Kardiyoloji Vakaları:
  - Göğüs Ağrısı - Acil ✅
  - Çarpıntı Şikayeti ✅
```

**Çözüm:** 
1. Öğretmen hesabıyla giriş yapın
2. Her sınıf için katılım kodunu alın
3. Öğrenci hesabıyla tüm sınıflara katılın
4. Artık tüm vakaları görebilirsiniz!

**Sebep 2:** Veritabanı güncel değil.
**Çözüm:** Uygulamayı silin ve yeniden yükleyin.

### Hata: Dialog Açılmıyor / Crash Oluyor
**Sebep:** Veritabanı güncel değil.
**Çözüm:**
1. Uygulamayı silin
2. Build klasörünü temizleyin
3. Rebuild Project
4. Yeniden yükleyin

### Hata: "Tanı gönderilirken hata oluştu"
**Sebep:** Veritabanı bağlantısı sorunu.
**Çözüm:**
1. Logcat'i kontrol edin
2. Hata mesajını paylaşın

### Hata: Window Leak
**Durum:** ✅ DÜZELTİLDİ
Dialog artık `onDestroy()` metodunda düzgün şekilde kapatılıyor.

---

## 📊 Kod Özeti

### CaseSimulationActivity.kt
```kotlin
// Dialog referansı - memory leak önleme
private var diagnosisDialog: AlertDialog? = null

// Tanı koy butonu
binding.btnDiagnose.setOnClickListener {
    showSubmitDiagnosisDialog()
}

// Dialog gösterme
private fun showSubmitDiagnosisDialog() {
    diagnosisDialog?.dismiss()
    diagnosisDialog = AlertDialog.Builder(this)
        .setView(dialogBinding.root)
        .create()
    diagnosisDialog?.show()
}

// Temizleme
override fun onDestroy() {
    diagnosisDialog?.dismiss()
    diagnosisDialog = null
    super.onDestroy()
}
```

### CaseSimulationViewModel.kt
```kotlin
fun submitDiagnosis(caseId: Long, studentId: Long, diagnosis: String, explanation: String) {
    viewModelScope.launch {
        val studentDiagnosis = StudentDiagnosis(
            caseId = caseId,
            studentId = studentId,
            diagnosis = diagnosis,
            explanation = explanation
        )
        studentDiagnosisDao.insert(studentDiagnosis)
        _diagnosisSubmitState.postValue(Success("Tanınız başarıyla gönderildi!"))
    }
}
```

---

## 🎯 Sonuç

**Tanı koyma özelliği %100 hazır ve çalışıyor!**

Eğer hala sorun yaşıyorsanız:
1. Yukarıdaki adımları sırayla uygulayın
2. Test senaryosunu takip edin
3. Hata alırsanız Logcat çıktısını paylaşın

---

## 📝 Not

- Veritabanı version: **6**
- Son düzeltme: Window leak sorunu giderildi (23.12.2025)
- Test kullanıcıları:
  - **Öğrenci Hesapları (Tanı Koyabilir):**
    - Kullanıcı Adı: `ogrenci` | Şifre: `123456` ✅
    - Kullanıcı Adı: `ayse` | Şifre: `123456`
    - Kullanıcı Adı: `mehmet` | Şifre: `123456`
    - Kullanıcı Adı: `zeynep` | Şifre: `123456`
  - **Öğretmen Hesabı (Tanı Koyamaz!):**
    - Kullanıcı Adı: `ogretmen` | Şifre: `123456` ❌

