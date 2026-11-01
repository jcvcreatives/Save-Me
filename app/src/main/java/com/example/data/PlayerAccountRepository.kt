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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

private val VALID_AUTH_METHODS = setOf("gmail", "account", "email", "google")

val JUNK_TEST_UIDS = setOf(
    "usr_asdasda",
    "usr_irbdhejebdiensishdbsiej",
    "usr_jrjdhdhdhisdj",
    "usr_jsjsjsj",
    "usr_ndjdjsjd",
    "usr_nsjsj",
    "usr_sdfdsfsd",
    "usr_udjdjd"
)

fun normalizeAuthMethod(raw: String?): String {
    val cleaned = raw?.trim()?.lowercase() ?: "gmail"
    return if (cleaned in VALID_AUTH_METHODS) cleaned else "gmail"
}

fun computePasswordHash(rawPassword: String?): String {
    val pwd = rawPassword?.trim().orEmpty()
    if (pwd.isEmpty()) return ""
    val input = "doodle_auth_v1:$pwd"
    var h1 = 0x811c9dc5.toInt()
    var h2 = 0x1505
    for (i in input.indices) {
        val ch = input[i].code
        h1 = (h1 xor ch) * 0x01000193
        h2 = (h2 * 33) xor ch
    }
    val hex1 = h1.toUInt().toString(16).padStart(8, '0')
    val hex2 = h2.toUInt().toString(16).padStart(8, '0')
    return "dh1_${hex1}_${hex2}"
}

@IgnoreExtraProperties
data class PlayerAccount(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Doodle Player",
    val email: String? = null,
    val passwordHash: String? = null,
    val authMethod: String = "gmail",
    val campaignLevelIndex: Long = 0L,
    val coins: Long = 100L,
    val perfectCount: Long = 0L,
    val currentStreak: Long = 0L,
    val maxStreak: Long = 0L,
    val completedLevelsCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val cleanEmail = email?.trim()?.take(150)?.takeIf { it.isNotEmpty() }
        val cleanPwdHash = passwordHash?.trim()?.take(128)?.takeIf { it.isNotEmpty() }
        val payload = mutableMapOf<String, Any>(
            "userId" to userId,
            "displayName" to displayName.trim().take(50).ifEmpty { "Doodle Player" },
            "authMethod" to normalizeAuthMethod(authMethod),
            "campaignLevelIndex" to campaignLevelIndex.coerceIn(0L, 1000L),
            "coins" to coins.coerceIn(0L, 10000000L),
            "perfectCount" to perfectCount.coerceIn(0L, 1000000L),
            "currentStreak" to currentStreak.coerceIn(0L, 1000000L),
            "maxStreak" to maxStreak.coerceIn(0L, 1000000L),
            "completedLevelsCount" to completedLevelsCount.coerceIn(0L, 1000L),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (cleanEmail != null) {
            payload["email"] = cleanEmail
        }
        if (cleanPwdHash != null) {
            payload["passwordHash"] = cleanPwdHash
        }
        return payload
    }

    fun toUpdateMap(): Map<String, Any> {
        val cleanEmail = email?.trim()?.take(150)?.takeIf { it.isNotEmpty() }
        val cleanPwdHash = passwordHash?.trim()?.take(128)?.takeIf { it.isNotEmpty() }
        val payload = mutableMapOf<String, Any>(
            "userId" to userId,
            "displayName" to displayName.trim().take(50).ifEmpty { "Doodle Player" },
            "authMethod" to normalizeAuthMethod(authMethod),
            "campaignLevelIndex" to campaignLevelIndex.coerceIn(0L, 1000L),
            "coins" to coins.coerceIn(0L, 10000000L),
            "perfectCount" to perfectCount.coerceIn(0L, 1000000L),
            "currentStreak" to currentStreak.coerceIn(0L, 1000000L),
            "maxStreak" to maxStreak.coerceIn(0L, 1000000L),
            "completedLevelsCount" to completedLevelsCount.coerceIn(0L, 1000L),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (cleanEmail != null) {
            payload["email"] = cleanEmail
        }
        if (cleanPwdHash != null) {
            payload["passwordHash"] = cleanPwdHash
        }
        return payload
    }
}

class PlayerAccountRepository(
    private val db: FirebaseFirestore
) {
    private val writeMutex = Mutex()

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
        if (userId in JUNK_TEST_UIDS) return@runCatching null
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

    suspend fun findPlayerAccountByIdentifier(
        identifier: String,
        passwordHashOpt: String? = null
    ): Result<PlayerAccount?> = runCatching {
        val raw = identifier.trim()
        val lower = raw.lowercase()
        if (lower.isEmpty()) return@runCatching null
        val slug = lower.replace(Regex("[^a-z0-9]"), "_").trim('_').take(32)
        if (slug.isEmpty()) return@runCatching null
        val directUid = if (lower.startsWith("usr_")) lower else "usr_$slug"
        if (directUid in JUNK_TEST_UIDS || lower in JUNK_TEST_UIDS) return@runCatching null
        val cleanPwdHash = passwordHashOpt?.trim()?.takeIf { it.isNotEmpty() }

        val matchesById = linkedMapOf<String, PlayerAccount>()
        fun addIfMatches(docId: String, account: PlayerAccount) {
            if (docId in JUNK_TEST_UIDS) return
            val resolvedId = account.userId.ifBlank { docId }
            if (resolvedId in JUNK_TEST_UIDS) return
            val emailLower = account.email?.trim()?.lowercase().orEmpty()
            val nameLower = account.displayName.trim().lowercase()
            val uidLower = resolvedId.lowercase()
            val docIdLower = docId.lowercase()
            val emailPrefix = emailLower.substringBefore("@")
            if (
                emailLower == lower ||
                nameLower == lower ||
                uidLower == lower ||
                uidLower == directUid ||
                docIdLower == directUid ||
                (emailPrefix.isNotEmpty() && emailPrefix == lower)
            ) {
                matchesById[docId] = account.copy(id = docId, userId = resolvedId)
            }
        }

        // 1. Direct UID lookup first (saves Firestore quota)
        try {
            val directSnap = db.collection(COLLECTION_PLAYERS).document(directUid).get().await()
            if (directSnap.exists()) {
                val acc = directSnap.toObject(
                    PlayerAccount::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                if (acc != null) {
                    addIfMatches(directUid, acc)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }

        val directCandidate = matchesById[directUid]
        val needCollectionScan = directCandidate == null ||
            (cleanPwdHash != null && !directCandidate.passwordHash.isNullOrBlank() && directCandidate.passwordHash.trim() != cleanPwdHash)

        // 2. Collection query only when needed
        if (needCollectionScan) {
            try {
                val querySnap = db.collection(COLLECTION_PLAYERS).get().await()
                for (docSnap in querySnap.documents) {
                    val docId = docSnap.id
                    val account = docSnap.toObject(
                        PlayerAccount::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ) ?: continue
                    addIfMatches(docId, account)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }

        val matches = matchesById.values.toMutableList()
        if (matches.isEmpty()) return@runCatching null

        matches.sortWith(
            Comparator { a, b ->
                if (cleanPwdHash != null) {
                    val aPassMatch = a.passwordHash?.trim() == cleanPwdHash
                    val bPassMatch = b.passwordHash?.trim() == cleanPwdHash
                    if (aPassMatch != bPassMatch) {
                        return@Comparator if (aPassMatch) -1 else 1
                    }
                }
                val aExactEmail = a.email?.trim()?.lowercase() == lower
                val bExactEmail = b.email?.trim()?.lowercase() == lower
                if (aExactEmail != bExactEmail) {
                    return@Comparator if (aExactEmail) -1 else 1
                }
                val aExactUid = a.id.lowercase() == directUid || a.userId.lowercase() == directUid
                val bExactUid = b.id.lowercase() == directUid || b.userId.lowercase() == directUid
                if (aExactUid != bExactUid) {
                    return@Comparator if (aExactUid) -1 else 1
                }
                b.campaignLevelIndex.compareTo(a.campaignLevelIndex)
            }
        )
        matches.first()
    }

    suspend fun createOrSyncPlayerAccount(
        displayName: String,
        email: String? = null,
        passwordHash: String? = null,
        authMethod: String = "gmail",
        campaignLevelIndex: Int = 0,
        coins: Int = 100,
        perfectCount: Int = 0,
        currentStreak: Int = 0,
        maxStreak: Int = 0,
        completedLevelsCount: Int = 0,
        userId: String = currentUid(),
        overwriteIfExists: Boolean = false
    ): Result<PlayerAccount> = runCatching {
        writeMutex.withLock {
            val uid = userId.ifBlank { currentUid() }
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
            val cleanEmail = email?.trim()?.take(150)?.takeIf { it.isNotEmpty() }
            val cleanPwdHash = passwordHash?.trim()?.take(128)?.takeIf { it.isNotEmpty() }
            val cleanAuth = normalizeAuthMethod(authMethod)

            if (!existingSnap.exists()) {
                val newAccount = PlayerAccount(
                    id = uid,
                    userId = uid,
                    displayName = cleanName,
                    email = cleanEmail,
                    passwordHash = cleanPwdHash,
                    authMethod = cleanAuth,
                    campaignLevelIndex = campaignLevelIndex.toLong().coerceIn(0L, 1000L),
                    coins = coins.toLong().coerceIn(0L, 10000000L),
                    perfectCount = perfectCount.toLong().coerceIn(0L, 1000000L),
                    currentStreak = currentStreak.toLong().coerceIn(0L, 1000000L),
                    maxStreak = maxStreak.toLong().coerceIn(0L, 1000000L),
                    completedLevelsCount = completedLevelsCount.toLong().coerceIn(0L, 1000L)
                )
                try {
                    docRef.set(newAccount.toCreateMap(), SetOptions.merge()).await()
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
                    id = uid,
                    userId = uid,
                    displayName = cleanName,
                    email = cleanEmail ?: existing.email,
                    passwordHash = cleanPwdHash ?: existing.passwordHash,
                    authMethod = cleanAuth,
                    campaignLevelIndex = if (overwriteIfExists) {
                        campaignLevelIndex.toLong().coerceIn(0L, 1000L)
                    } else {
                        maxOf(existing.campaignLevelIndex, campaignLevelIndex.toLong()).coerceIn(0L, 1000L)
                    },
                    coins = if (overwriteIfExists) {
                        coins.toLong().coerceIn(0L, 10000000L)
                    } else {
                        maxOf(existing.coins, coins.toLong()).coerceIn(0L, 10000000L)
                    },
                    perfectCount = if (overwriteIfExists) {
                        perfectCount.toLong().coerceIn(0L, 1000000L)
                    } else {
                        maxOf(existing.perfectCount, perfectCount.toLong()).coerceIn(0L, 1000000L)
                    },
                    currentStreak = if (overwriteIfExists) {
                        currentStreak.toLong().coerceIn(0L, 1000000L)
                    } else {
                        maxOf(existing.currentStreak, currentStreak.toLong()).coerceIn(0L, 1000000L)
                    },
                    maxStreak = if (overwriteIfExists) {
                        maxStreak.toLong().coerceIn(0L, 1000000L)
                    } else {
                        maxOf(existing.maxStreak, maxStreak.toLong()).coerceIn(0L, 1000000L)
                    },
                    completedLevelsCount = if (overwriteIfExists) {
                        completedLevelsCount.toLong().coerceIn(0L, 1000L)
                    } else {
                        maxOf(existing.completedLevelsCount, completedLevelsCount.toLong()).coerceIn(0L, 1000L)
                    }
                )
                try {
                    docRef.set(merged.toUpdateMap(), SetOptions.merge()).await()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    handleFirestoreError(e, OperationType.UPDATE, docRef.path)
                    throw e
                }
                merged
            }
        }
    }

    suspend fun updateProgress(
        displayName: String,
        email: String?,
        passwordHash: String? = null,
        authMethod: String,
        campaignLevelIndex: Int,
        coins: Int,
        perfectCount: Int,
        currentStreak: Int,
        maxStreak: Int,
        completedLevelsCount: Int,
        userId: String = currentUid()
    ): Result<PlayerAccount> = runCatching {
        writeMutex.withLock {
            val uid = userId.ifBlank { currentUid() }
            val docRef = db.collection(COLLECTION_PLAYERS).document(uid)
            val cleanName = displayName.trim().take(50).ifEmpty { "Doodle Player" }
            val cleanEmail = email?.trim()?.take(150)?.takeIf { it.isNotEmpty() }
            val cleanPwdHash = passwordHash?.trim()?.take(128)?.takeIf { it.isNotEmpty() }
            val cleanAuth = normalizeAuthMethod(authMethod)
            val updated = PlayerAccount(
                id = uid,
                userId = uid,
                displayName = cleanName,
                email = cleanEmail,
                passwordHash = cleanPwdHash,
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
                    docRef.set(updated.toUpdateMap(), SetOptions.merge()).await()
                } else {
                    docRef.set(updated.toCreateMap(), SetOptions.merge()).await()
                }
                updated
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, docRef.path)
                throw e
            }
        }
    }

    companion object {
        const val COLLECTION_PLAYERS = "players"
    }
}

