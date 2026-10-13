package es.scoutsafa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.User
import es.scoutsafa.app.domain.model.UserRole
import es.scoutsafa.app.notifications.FcmTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onFullNameChanged(value: String) { _fullName.value = value }
    fun onEmailChanged(value: String) { _email.value = value }
    fun onPasswordChanged(value: String) { _password.value = value }
    fun onConfirmPasswordChanged(value: String) { _confirmPassword.value = value }

    fun register() {
        viewModelScope.launch {
            val name = _fullName.value.trim()
            val mail = _email.value.trim()

            when {
                name.isEmpty() || mail.isEmpty() || _password.value.isEmpty() ->
                    { _errorMessage.value = "Por favor completa todos los campos"; return@launch }
                _password.value.length < 6 ->
                    { _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"; return@launch }
                _password.value != _confirmPassword.value ->
                    { _errorMessage.value = "Las contraseñas no coinciden"; return@launch }
            }

            _isLoading.value = true
            authRepository.register(mail, _password.value).fold(
                onSuccess = { firebaseUser ->
                    val now = System.currentTimeMillis()
                    val saved = userRepository.save(
                        User(
                            id = firebaseUser.uid,
                            fullName = name,
                            email = mail,
                            // Todos entran como familia; los scouters se promocionan desde Firestore.
                            role = UserRole.FAMILIA.name,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                    if (saved) {
                        fcmTokenManager.refreshAndSaveToken(firebaseUser.uid)
                        _registerSuccess.value = true
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value = "No se pudo guardar tu perfil. Inténtalo de nuevo."
                    }
                },
                onFailure = { e -> _errorMessage.value = mapFirebaseError(e.message) }
            )
            _isLoading.value = false
        }
    }

    fun clearError() { _errorMessage.value = null }

    private fun mapFirebaseError(message: String?): String = when {
        message == null -> "Error desconocido"
        "already in use" in message.lowercase() -> "Ya existe una cuenta con ese correo"
        "badly formatted" in message.lowercase() -> "El formato del correo no es válido"
        "network" in message.lowercase() -> "Error de conexión"
        else -> "Error al crear la cuenta"
    }
}
