package es.scoutsafa.app.data.repositories

import es.scoutsafa.app.data.remote.FirestoreCollections
import es.scoutsafa.app.domain.model.ScoutActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ActivityRepository(firestore: FirebaseFirestore) {

    private val collection = firestore.collection(FirestoreCollections.ACTIVITIES)

    fun listAll(): Flow<List<ScoutActivity>> = callbackFlow {
        val listener = collection
            .orderBy("startTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(ScoutActivity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getById(id: String): Result<ScoutActivity> {
        return try {
            val doc = collection.document(id).get().await()
            val activity = doc.toObject(ScoutActivity::class.java)?.copy(id = doc.id)
                ?: return Result.failure(Exception("Actividad no encontrada"))
            Result.success(activity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun save(activity: ScoutActivity): Boolean {
        return try {
            collection.add(activity).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun update(activity: ScoutActivity): Boolean {
        return try {
            collection.document(activity.id).set(activity).await()
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
