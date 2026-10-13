package es.scoutsafa.app.data.repositories

import es.scoutsafa.app.data.remote.FirestoreCollections
import es.scoutsafa.app.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository(firestore: FirebaseFirestore) {

    private val collection = firestore.collection(FirestoreCollections.USERS)

    suspend fun getById(id: String): Result<User> {
        return try {
            val doc = collection.document(id).get().await()
            val user = doc.toObject(User::class.java)?.copy(id = doc.id)
                ?: return Result.failure(Exception("Usuario no encontrado"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeById(id: String): Flow<User?> = callbackFlow {
        val listener = collection.document(id).addSnapshotListener { doc, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            trySend(doc?.toObject(User::class.java)?.copy(id = doc.id))
        }
        awaitClose { listener.remove() }
    }

    suspend fun save(user: User): Boolean {
        return try {
            collection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** Actualiza solo los campos indicados (merge), sin tocar el rol. */
    suspend fun updateProfile(userId: String, phone: String, section: String): Boolean {
        return try {
            collection.document(userId).set(
                mapOf(
                    "phone" to phone,
                    "section" to section,
                    "onboardingDone" to true,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            ).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateFcmToken(userId: String, token: String) {
        try {
            collection.document(userId)
                .set(mapOf("fcmToken" to token), SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
