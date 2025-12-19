package com.example.eee339_android_proje.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eee339_android_proje.data.AppDatabase
import com.example.eee339_android_proje.data.entity.User
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(username: String, password: String, confirmPassword: String, role: String) {
        // Boş alan kontrolü
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerState.value = RegisterState.Error("Lütfen tüm alanları doldurun")
            return
        }

        // Kullanıcı adı uzunluk kontrolü
        if (username.length < 3) {
            _registerState.value = RegisterState.Error("Kullanıcı adı en az 3 karakter olmalıdır")
            return
        }

        // Şifre uzunluk kontrolü
        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Şifre en az 6 karakter olmalıdır")
            return
        }

        // Şifre eşleşme kontrolü
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Şifreler eşleşmiyor")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Kullanıcı adı kontrolü
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    _registerState.postValue(RegisterState.Error("Bu kullanıcı adı zaten kullanılıyor"))
                    _isLoading.postValue(false)
                    return@launch
                }

                // Yeni kullanıcı oluştur
                val newUser = User(
                    username = username,
                    password = password,
                    role = role
                )

                val userId = userDao.insert(newUser)

                if (userId > 0) {
                    _registerState.postValue(RegisterState.Success("Kayıt başarılı! Giriş yapabilirsiniz"))
                } else {
                    _registerState.postValue(RegisterState.Error("Kayıt yapılırken hata oluştu"))
                }
            } catch (e: Exception) {
                _registerState.postValue(RegisterState.Error("Kayıt yapılırken hata oluştu: ${e.message}"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    sealed class RegisterState {
        data class Success(val message: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}

