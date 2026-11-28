package com.upchiapas.fittrack

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.upchiapas.fittrack.core.session.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthSessionInstrumentedTest {
    @Test fun encryptedSessionRoundtripAndKeyDeletion() = runBlocking {
        val cipher = SessionCipher()
        val store = ProtectedSessionStore(InstrumentationRegistry.getInstrumentation().targetContext, cipher)
        store.clear()
        try {
            val first = cipher.encrypt("test-only-token")
            val second = cipher.encrypt("test-only-token")
            assertNotEquals(first, second)
            assertEquals("test-only-token", cipher.decrypt(first))
            val session = StoredSession("test-only-token", 123456)
            store.write(session)
            assertEquals(session, store.read())
            store.clear()
            assertNull(store.read())
            try { cipher.decrypt(first); fail("Old ciphertext must be unusable") } catch (_: java.security.GeneralSecurityException) { }
        } finally { store.clear() }
    }
}
