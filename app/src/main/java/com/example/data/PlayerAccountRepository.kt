package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@IgnoreExtraProperties
data class PlayerAccount(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Doodle Player",
    val email: String? = null,
    val authMethod: String = "gmail",
    val campaignLevelIndex: Long = 0L,
    val coins: Long = 1231L,
    val perfectCount: Long = 3L,
    val currentStreak: Long = 2L,
    val maxStreak: Long = 5L,
    val completedLevelsCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "displayName" to displayName.trim().take(50).ifEmpty { "Doodle Player" },
            "email" to email?.trim()?.take(150)?.ifEmpty { null },
            "authMethod" to if (authMethod == "account") "account" else "gmail",
            "campaignLevelIndex" to campaignLevelIndex.coerceIn(0L, 1000L),
            "coins" to coins.coerceIn(0L, 10000000L),
            "perfectCount" to perfectCount.coerceIn(0L, 1000000L),
            "currentStreak" to currentStreak.coerceIn(0L, 1000000L),
            "maxStreak" to maxStreak.coerceIn(0L, 1000000L),
            "completedLevelsCount" to completedLevelsCount.coerceIn(0L, 1000L),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null } as Map<String, Any>
    }

    fun toUpdateMap(): Map<String, Any> {
        return mapOf(
            "displayName" to displayName.trim().take(50).ifEmpty { "Doodle Player" },
            "email" to email?.trim()?.take(150)?.ifEmpty { null },
            "authMethod" to if (authMethod == "account") "account" else "gmail",
            "campaignLevelIndex" to campaignLevelIndex.coerceIn(0L, 1000L),
            "coins" to coins.coerceIn(0L, 10000000L),
            "perfectCount" to perfectCount.coerceIn(0L, 1000000L),
            "currentStreak" to currentStreak.coerceIn(0L, 1000000L),
            "maxStreak" to maxStreak.coerceIn(0L, 1000000L),
            "completedLevelsCount" to completedLevelsCount.coerceIn(0L, 1000L),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null } as Map<String, Any>
    }
}

class PlayerAccountRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun currentUid(): String {
        return checkNotNull(FirebaseAuth.getInstance().currentUser?.uid) {
            "User must be authenticated before accessing PlayerAccountRepository"
        }
    }

    fun observePlayerAccount(userId: String = currentUid()): Flow<PlayerAccount?> {
        return db.collection(COLLECTION_PLAYERS)
            .document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        PlayerAccount::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }
    }

    suspend fun getPlayerAccount(userId: String = currentUid()): Result<PlayerAccount?> = runCatching {
        val docRef = db.collection(COLLECTION_PLAYERS).document(userId)
        try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                snapshot.toObject(
                    PlayerAccount::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun createOrSyncPlayerAccount(
        displayName: String,
        email: String? = null,
        authMethod: String = "gmail",
        campaignLevelIndex: Int = 0,
        coins: Int = 1231,
        perfectCount: Int = 3,
        currentStreak: Int = 2,
        maxStreak: Int = 5,
        completedLevelsCount: Int = 0
    ): Result<PlayerAccount> = runCatching {
        val uid = currentUid()
        val docRef = db.collection(COLLECTION_PLAYERS).document(uid)
        val existingSnap = try {
            docRef.get().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }

        val cleanName = displayName.trim().take(50).ifEmpty { "Doodle Player" }
        val cleanAuth = if (authMethod == "account") "account" else "gmail"

        if (!existingSnap.exists()) {
            val newAccount = PlayerAccount(
                id = uid,
                userId = uid,
                displayName = cleanName,
                email = email,
                authMethod = cleanAuth,
                campaignLevelIndex = campaignLevelIndex.toLong(),
                coins = coins.toLong(),
                perfectCount = perfectCount.toLong(),
                currentStreak = currentStreak.toLong(),
                maxStreak = maxStreak.toLong(),
                completedLevelsCount = completedLevelsCount.toLong()
            )
            try {
                docRef.set(newAccount.toCreateMap()).await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
                throw e
            }
            newAccount
        } else {
            val existing = existingSnap.toObject(
                PlayerAccount::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: PlayerAccount(id = uid, userId = uid)
            val merged = existing.copy(
                displayName = cleanName,
                email = email ?: existing.email,
                authMethod = cleanAuth,
                campaignLevelIndex = maxOf(existing.campaignLevelIndex, campaignLevelIndex.toLong()),
                coins = maxOf(existing.coins, coins.toLong()),
                perfectCount = maxOf(existing.perfectCount, perfectCount.toLong()),
                currentStreak = maxOf(existing.currentStreak, currentStreak.toLong()),
                maxStreak = maxOf(existing.maxStreak, maxStreak.toLong()),
                completedLevelsCount = maxOf(existing.completedLevelsCount, completedLevelsCount.toLong())
            )
            try {
                docRef.update(merged.toUpdateMap()).await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, docRef.path)
                throw e
            }
            merged
        }
    }

    suspend fun updateProgress(
        displayName: String,
        email: String?,
        authMethod: String,
        campaignLevelIndex: Int,
        coins: Int,
        perfectCount: Int,
        currentStreak: Int,
        maxStreak: Int,
        completedLevelsCount: Int,
        userId: String = currentUid()
    ): Result<PlayerAccount> = runCatching {
        val docRef = db.collection(COLLECTION_PLAYERS).document(userId)
        val cleanName = displayName.trim().take(50).ifEmpty { "Doodle Player" }
        val cleanAuth = if (authMethod == "account") "account" else "gmail"
        val updated = PlayerAccount(
            id = userId,
            userId = userId,
            displayName = cleanName,
            email = email,
            authMethod = cleanAuth,
            campaignLevelIndex = campaignLevelIndex.toLong().coerceIn(0L, 1000L),
            coins = coins.toLong().coerceIn(0L, 10000000L),
            perfectCount = perfectCount.toLong().coerceIn(0L, 1000000L),
            currentStreak = currentStreak.toLong().coerceIn(0L, 1000000L),
            maxStreak = maxStreak.toLong().coerceIn(0L, 1000000L),
            completedLevelsCount = completedLevelsCount.toLong().coerceIn(0L, 1000L)
        )
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(updated.toUpdateMap()).await()
            } else {
                docRef.set(updated.toCreateMap()).await()
            }
            updated
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    companion object {
        const val COLLECTION_PLAYERS = "players"
    }
}
