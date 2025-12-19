# 🔧 Giriş Sorunu Çözümü

## Sorun: "Geçersiz kullanıcı adı veya şifre" hatası

Eğer demo hesaplarla (`ogretmen/123456` veya `ogrenci/123456`) giriş yaparken "Geçersiz kullanıcı adı veya şifre" hatası alıyorsanız, bunun nedeni veritabanının eski versiyonda kalması olabilir.

## ✅ Çözümler

### Çözüm 1: Uygulamayı Yeniden Yükle (Önerilen)

1. Android Studio'da uygulamayı tamamen durdurun
2. Telefon/emülatörde ayarlara gidin
3. **Ayarlar > Uygulamalar > C-learn**
4. **Depolama** seçeneğine tıklayın
5. **Verileri Temizle** ve **Önbelleği Temizle** butonlarına basın
6. Uygulamayı tekrar çalıştırın

### Çözüm 2: Uygulamayı Kaldır ve Tekrar Yükle

1. Telefon/emülatörden C-learn uygulamasını kaldırın
2. Android Studio'dan uygulamayı tekrar çalıştırın (Run butonuna basın)
3. Uygulama yeni veritabanı ile açılacak ve demo hesaplar otomatik oluşturulacaktır

### Çözüm 3: Gradle Clean ve Rebuild

1. Android Studio'da: **Build > Clean Project**
2. Bekleyin, ardından: **Build > Rebuild Project**
3. Uygulamayı tekrar çalıştırın

## 📱 Demo Hesaplar

Veritabanı doğru oluşturulduysa şu hesaplarla giriş yapabilirsiniz:

### Öğretmen Hesapları
- **Kullanıcı Adı:** `ogretmen` | **Şifre:** `123456`
- **Kullanıcı Adı:** `drahmet` | **Şifre:** `123456`

### Öğrenci Hesapları
- **Kullanıcı Adı:** `ogrenci` | **Şifre:** `123456`
- **Kullanıcı Adı:** `ayse` | **Şifre:** `123456`
- **Kullanıcı Adı:** `mehmet` | **Şifre:** `123456`
- **Kullanıcı Adı:** `zeynep` | **Şifre:** `123456`

## 🐛 Debug Bilgisi

Eğer sorun devam ediyorsa:

1. Hata mesajını dikkatlice okuyun
2. "Veritabanında kullanıcı yok" mesajı görüyorsanız → Çözüm 1 veya 2'yi uygulayın
3. Hata detaylarını kontrol edin (Android Studio'nun Logcat penceresinden)

## 📝 Teknik Detaylar

- **Veritabanı Versiyonu:** 2
- **Veritabanı Adı:** clearn_database
- **Migration Stratejisi:** fallbackToDestructiveMigration (versiyon değişikliklerinde veritabanı yeniden oluşturulur)
- **Demo Veri Oluşturma:** SeedDatabaseCallback ile otomatik

## ⚠️ Not

Eğer yukarıdaki çözümlerden hiçbiri işe yaramazsa:

1. Emülatör/telefonu yeniden başlatın
2. Android Studio'yu yeniden başlatın
3. Gradle Sync yapın: **File > Sync Project with Gradle Files**

