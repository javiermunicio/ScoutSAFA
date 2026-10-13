package es.scoutsafa.app.notifications

import es.scoutsafa.app.data.repositories.AuthRepository
import es.scoutsafa.app.data.repositories.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ScoutFirebaseMessagingService : FirebaseMessagingService() {

    private val authRepository: AuthRepository by inject()
    private val userRepository: UserRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        val userId = authRepository.currentUser?.uid ?: return
        scope.launch { userRepository.updateFcmToken(userId, token) }
    }

    /**
     * Se llama con la app en primer plano. En segundo plano el sistema muestra
     * directamente la notificación que viene en el payload.
     * Si el mensaje incluye data["type"] = "activity" se usa el canal de actividades.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: return
        val body = message.notification?.body ?: message.data["body"].orEmpty()

        if (message.data["type"] == "activity") {
            NotificationHelper.showActivityNotification(this, title, body)
        } else {
            NotificationHelper.showAnnouncementNotification(this, title, body)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
