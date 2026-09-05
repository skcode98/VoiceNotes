package com.voicenotes.app.data.cloud

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseCloudService(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun signUp(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                Result.success(result.user?.uid ?: "")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun signIn(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Result.success(result.user?.uid ?: "")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun uploadNoteMetadata(noteId: String, metadata: Map<String, Any>): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                firestore.collection("notes").document(noteId)
                    .set(metadata).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun uploadAudioFile(noteId: String, audioFilePath: String): Result<String> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val fileRef = storage.reference.child("audio/$noteId.m4a")
                val uploadTask = fileRef.putFile(android.net.Uri.parse(audioFilePath)).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()
                Result.success(downloadUrl)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun downloadNoteMetadata(noteId: String): Result<Map<String, Any>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val doc = firestore.collection("notes").document(noteId).get().await()
                Result.success(doc.data ?: emptyMap())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun logout(): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                auth.signOut()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
