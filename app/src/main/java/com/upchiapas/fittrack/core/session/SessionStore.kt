package com.upchiapas.fittrack.core.session

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.sessionDataStore by preferencesDataStore("protected_session")
interface SessionStore {
    suspend fun read(): StoredSession?
    suspend fun write(session: StoredSession)
    suspend fun clear()
}
data class StoredSession(val token: String, val expiresAt: Long)
class SessionCipher {
    private val alias = "fittrack_session_v1"
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return (store.getKey(alias, null) as? SecretKey) ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").run {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
            generateKey()
        }
    }
    fun encrypt(value: String): String = Cipher.getInstance("AES/GCM/NoPadding").run {
        init(Cipher.ENCRYPT_MODE, key())
        Base64.encodeToString(iv + doFinal(value.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }
    fun decrypt(value: String): String = Cipher.getInstance("AES/GCM/NoPadding").run {
        val bytes = Base64.decode(value, Base64.NO_WRAP)
        require(bytes.size > 28)
        init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
        doFinal(bytes.copyOfRange(12, bytes.size)).toString(Charsets.UTF_8)
    }
    fun invalidate() { KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(alias) } }
}
class ProtectedSessionStore(context: Context, private val cipher: SessionCipher) : SessionStore {
    private val store = context.sessionDataStore
    private val value = stringPreferencesKey("encrypted")
    override suspend fun read(): StoredSession? = withContext(Dispatchers.IO) {
        store.data.first()[value]?.let {
            val parts = cipher.decrypt(it).split("\n", limit = 2)
            StoredSession(parts[1], parts[0].toLong())
        }
    }
    override suspend fun write(session: StoredSession): Unit = withContext(Dispatchers.IO) {
        val encrypted = cipher.encrypt("${session.expiresAt}\n${session.token}")
        store.edit { it[value] = encrypted }
        Unit
    }
    override suspend fun clear(): Unit = withContext(Dispatchers.IO) {
        // Key deletion first makes any residual ciphertext unusable if DataStore deletion fails.
        cipher.invalidate()
        store.edit { it.clear() }
        Unit
    }
}
