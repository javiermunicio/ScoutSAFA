package es.scoutsafa.app.notifications

import es.scoutsafa.app.data.repositories.UserRepository
import es.scoutsafa.app.domain.model.Section
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FcmTokenManager(
    private val userRepository: UserRepository,
    private val messaging: FirebaseMessaging
) {
    suspend fun refreshAndSaveToken(userId: String) {
        try {
            val token = messaging.token.await()
            userRepository.updateFcmToken(userId, token)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Todos los usuarios reciben el topic "grupo"; además se suscriben al topic de su rama
     * (section_SCOUTS, section_ROVERS...) y se desuscriben de las demás.
     * Los avisos se envían a esos topics desde la consola de Firebase o una Cloud Function.
     */
    suspend fun updateTopics(section: String?) {
        try {
            messaging.subscribeToTopic(TOPIC_GROUP).await()
            Section.entries.forEach { s ->
                if (s.name == section) {
                    messaging.subscribeToTopic(sectionTopic(s)).await()
                } else {
                    messaging.unsubscribeFromTopic(sectionTopic(s)).await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TOPIC_GROUP = "grupo"
        fun sectionTopic(section: Section) = "section_${section.name}"
    }
}
