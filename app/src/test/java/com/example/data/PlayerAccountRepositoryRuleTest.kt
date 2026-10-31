package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PlayerAccountRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private fun uniqueEmail(prefix: String): String =
        "${prefix}_${System.nanoTime()}@test.com"

    @Test
    fun createOrSyncPlayerAccount_validPayload_createsAndReadsAccount() = runBlocking {
        val email = uniqueEmail("alice_create")
        val uid = signInTestUser(email)
        val repository = PlayerAccountRepository(firestore)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createOrSyncPlayerAccount(
                displayName = "Alice Sketch",
                email = email,
                authMethod = "gmail",
                campaignLevelIndex = 5,
                coins = 1500
            )
        }
        assertTrue("Expected account creation to succeed", createResult.isSuccess)
        val created = createResult.getOrThrow()
        assertEquals(uid, created.userId)
        assertEquals("Alice Sketch", created.displayName)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getPlayerAccount(uid).getOrThrow()
        }
        assertNotNull(fetched)
        assertEquals("Alice Sketch", fetched?.displayName)
        assertEquals(1500L, fetched?.coins)
    }

    @Test
    fun observePlayerAccount_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val email = uniqueEmail("alice_realtime")
        val uid = signInTestUser(email)
        val repository = PlayerAccountRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createOrSyncPlayerAccount(
                displayName = "Realtime Alice",
                email = email,
                authMethod = "account",
                campaignLevelIndex = 12,
                coins = 2000
            ).getOrThrow()
        }

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observePlayerAccount(uid).first { it != null && it.displayName == "Realtime Alice" }
        }
        assertNotNull(emitted)
        assertEquals(12L, emitted?.campaignLevelIndex)
    }

    @Test
    fun getPlayerAccount_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceEmail = uniqueEmail("alice_priv")
        val bobEmail = uniqueEmail("bob_priv")
        val aliceUid = signInTestUser(aliceEmail)
        val aliceRepo = PlayerAccountRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createOrSyncPlayerAccount(
                displayName = "Alice Private",
                email = aliceEmail,
                authMethod = "gmail"
            ).getOrThrow()
        }

        signInTestUser(bobEmail)
        val bobRepo = PlayerAccountRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                bobRepo.getPlayerAccount(aliceUid).getOrThrow()
            }
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun observePlayerAccount_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(uniqueEmail("alice_unauth"))
        auth.signOut()
        val repository = PlayerAccountRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observePlayerAccount(aliceUid).first()
            }
            fail("Expected exception on unauthenticated observation")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    @Test
    fun updateProgress_stateChange_keepsFirestoreInParity() = runBlocking {
        val email = uniqueEmail("alice_parity")
        val uid = signInTestUser(email)
        val repository = PlayerAccountRepository(firestore)

        // 1. First state change creates the document if not present
        val initialSync = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateProgress(
                displayName = "Parity Alice",
                email = email,
                authMethod = "account",
                campaignLevelIndex = 7,
                coins = 1400,
                perfectCount = 4,
                currentStreak = 3,
                maxStreak = 6,
                completedLevelsCount = 7,
                userId = uid
            ).getOrThrow()
        }
        assertEquals(7L, initialSync.campaignLevelIndex)
        assertEquals(1400L, initialSync.coins)

        // 2. Subsequent state change updates the document and maintains strict parity
        val updatedSync = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.updateProgress(
                displayName = "Parity Alice",
                email = email,
                authMethod = "account",
                campaignLevelIndex = 8,
                coins = 1450,
                perfectCount = 5,
                currentStreak = 4,
                maxStreak = 6,
                completedLevelsCount = 8,
                userId = uid
            ).getOrThrow()
        }
        assertEquals(8L, updatedSync.campaignLevelIndex)
        assertEquals(1450L, updatedSync.coins)

        val remote = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getPlayerAccount(uid).getOrThrow()
        }
        assertNotNull(remote)
        assertEquals(8L, remote?.campaignLevelIndex)
        assertEquals(1450L, remote?.coins)
        assertEquals(5L, remote?.perfectCount)
        assertEquals(8L, remote?.completedLevelsCount)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
