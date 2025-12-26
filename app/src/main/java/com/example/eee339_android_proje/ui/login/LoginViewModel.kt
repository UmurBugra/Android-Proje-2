package com.example.eee339_android_proje.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.User
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userDao = AppDatabase.getDatabase(application).userDao()
    
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Lütfen tüm alanları doldurun")
            return
        }
        
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Veritabanının hazır olup olmadığını birkaç kez kontrol et
                var retryCount = 0
                var userCount = 0

                while (retryCount < 3) {
                    userCount = userDao.getUserCount()
                    if (userCount > 0) break
                    kotlinx.coroutines.delay(1000)
                    retryCount++
                }

                if (userCount == 0) {
                    _loginState.postValue(LoginState.Error("Veritabanı yükleniyor. Lütfen 2-3 saniye bekleyip tekrar deneyin."))
                    _isLoading.postValue(false)
                    return@launch
                }

                val user = userDao.login(username, password)
                if (user != null) {
                    _loginState.postValue(LoginState.Success(user))
                } else {
                    // Kullanıcı bulunamadı - detaylı hata mesajı
                    val existingUser = userDao.getUserByUsername(username)
                    if (existingUser != null) {
                        _loginState.postValue(LoginState.Error("Şifre hatalı. Lütfen tekrar deneyin."))
                    } else {
                        _loginState.postValue(LoginState.Error("Kullanıcı bulunamadı. Demo hesaplar: ogretmen/123456 veya ogrenci/123456"))
                    }
                }
            } catch (e: Exception) {
                _loginState.postValue(LoginState.Error("Giriş yapılırken hata oluştu: ${e.message}"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    sealed class LoginState {
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
