package com.upchiapas.fittrack

import com.upchiapas.fittrack.features.auth.domain.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AuthUseCasesTest {
    private class Fake : AuthRepository {
        var login: LoginInput? = null
        var register: RegisterInput? = null
        override suspend fun login(input: LoginInput) { login = input }
        override suspend fun register(input: RegisterInput) { register = input }
    }
    @Test fun normalizesEmailWithoutChangingPassword() = runTest {
        val fake = Fake()
        LoginUseCase(fake)(LoginInput(" Test@Example.COM ", "  exact  "))
        assertEquals(LoginInput("test@example.com", "  exact  "), fake.login)
    }
    @Test fun registersBothRolesAndNormalizesUsername() = runTest {
        for (code in listOf("test-athlete-invitation", "test-trainer-invitation")) {
            val fake = Fake()
            RegisterUseCase(fake)(RegisterInput(" Ana ", " ANA.Test ", " A@Example.COM ", " exactpass ", " exactpass ", code))
            assertEquals("ana.test", fake.register!!.username)
            assertEquals("Ana", fake.register!!.name)
            assertEquals(" exactpass ", fake.register!!.password)
            assertEquals(code, fake.register!!.invitationCode)
        }
    }
    @Test fun rejectsInvalidRegistration() {
        val valid = RegisterInput("Ana", "ana", "ana@example.com", "12345678", "12345678", "test-invitation-code")
        for (input in listOf(valid.copy(invitationCode = ""), valid.copy(invitationCode = " "), valid.copy(name = " "), valid.copy(username = "@ana"), valid.copy(username = "áaa"),
            valid.copy(email = "invalid"), valid.copy(password = "short"), valid.copy(confirmation = "different"), valid.copy(password = "x".repeat(129)))) assertFalse(AuthValidation.valid(input))
        assertTrue(AuthValidation.valid(valid))
    }
    @Test fun loginPasswordHasDifferentMinimum() {
        assertTrue(AuthValidation.valid(LoginInput("a@example.com", " ")))
        assertFalse(AuthValidation.valid(LoginInput("a@example.com", "")))
    }
    @Test fun invalidInputDoesNotCallRepository() = runTest {
        val fake = Fake()
        try { LoginUseCase(fake)(LoginInput()); fail() } catch (e: AuthFailure) { assertEquals(AuthError.Validation, e.reason) }
        assertNull(fake.login)
    }
}
