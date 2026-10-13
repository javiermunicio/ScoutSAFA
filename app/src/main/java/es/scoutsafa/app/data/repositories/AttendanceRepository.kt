package es.scoutsafa.app.data.repositories

import es.scoutsafa.app.data.remote.FirestoreCollections
import es.scoutsafa.app.domain.model.ActivityAttendance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AttendanceRepository(firestore: FirebaseFirestore) {

    private val collection = firestore.collection(FirestoreCollections.ATTENDANCES)

    fun observeByActivity(activityId: String): Flow<List<ActivityAttendance>> = callbackFlow {
        val listener = collection
            .whereEqualTo("activityId", activityId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(ActivityAttendance::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    /** El id del documento es activityId_userId, así que confirmar/rechazar varias veces no duplica filas. */
    suspend fun set(attendance: ActivityAttendance): Boolean {
        return try {
            val docId = "${attendance.activityId}_${attendance.userId}"
            collection.document(docId).set(attendance.copy(id = docId)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun remove(activityId: String, userId: String): Boolean {
        return try {
            collection.document("${activityId}_$userId").delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
