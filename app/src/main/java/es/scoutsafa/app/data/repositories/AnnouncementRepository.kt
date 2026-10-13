package es.scoutsafa.app.data.repositories

import es.scoutsafa.app.data.remote.FirestoreCollections
import es.scoutsafa.app.domain.model.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AnnouncementRepository(firestore: FirebaseFirestore) {

    private val collection = firestore.collection(FirestoreCollections.ANNOUNCEMENTS)

    fun listAll(): Flow<List<Announcement>> = callbackFlow {
        val listener = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(Announcement::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun save(announcement: Announcement): Boolean {
        return try {
            collection.add(announcement).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun delete(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
