package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.notifications.FcmTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onEmailChanged(newEmail: String) { _email.value = newEmail }
    fun onPasswordChanged(newPassword: String) { _password.value = newPassword }

    fun login() {
        viewModelScope.launch {
            if (_email.value.isBlank() || _password.value.isEmpty()) {
                _errorMessage.value = "Por favor completa todos los campos"
                return@launch
            }
            _isLoading.value = true
            val result = authRepository.login(_email.value.trim(), _password.value)
            result.fold(
                onSuccess = { user ->
                    fcmTokenManager.refreshAndSaveToken(user.uid)
                    val section = userRepository.getById(user.uid).getOrNull()?.section
                    fcmTokenManager.updateTopics(section)

                    _loginSuccess.value = true
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = mapFirebaseError(e.message)
                    _loginSuccess.value = false
                }
            )
            _isLoading.value = false
        }
    }

    fun clearError() { _errorMessage.value = null }

    private fun mapFirebaseError(message: String?): String = when {
        message == null -> "Error desconocido"
        "password" in message.lowercase() -> "Contraseña incorrecta"
        "no user" in message.lowercase() -> "No existe una cuenta con ese correo"
        "badly formatted" in message.lowercase() -> "El formato del correo no es válido"
        "network" in message.lowercase() -> "Error de conexión"
        else -> "Error al iniciar sesión"
    }
}
