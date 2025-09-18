package com.mcu.bankapp.data.repository

import com.mcu.bankapp.domain.models.UserAccount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class UserRepositoryImplTest {

    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setUp() {
        userRepository = UserRepositoryImpl()
    }

    @Test
    fun `getUserAccount returns initial user account`() = runTest {
        // When
        val userAccount = userRepository.getUserAccount().first()

        // Then
        assertEquals("USER_001", userAccount.id)
        assertEquals("John Maxwell", userAccount.name)
        assertEquals("1234567890", userAccount.accountNumber)
        assertEquals(5000.0, userAccount.balance, 0.001)
        assertNull(userAccount.profileImageUrl)
    }

    @Test
    fun `getUserAccount returns Flow with current state`() = runTest {
        // Given
        val newAccount = UserAccount(
            id = "USER_002",
            name = "Jane Doe",
            accountNumber = "0987654321",
            balance = 3000.0,
            profileImageUrl = "https://example.com/profile.jpg"
        )

        // When
        userRepository.updateUserAccount(newAccount)
        val retrievedAccount = userRepository.getUserAccount().first()

        // Then
        assertEquals(newAccount, retrievedAccount)
    }

    @Test
    fun `updateUserAccount updates the account`() = runTest {
        // Given
        val newAccount = UserAccount(
            id = "USER_003",
            name = "Alice Smith",
            accountNumber = "1111222233",
            balance = 10000.0,
            profileImageUrl = "https://example.com/alice.jpg"
        )

        // When
        userRepository.updateUserAccount(newAccount)
        val updatedAccount = userRepository.getUserAccount().first()

        // Then
        assertEquals(newAccount.id, updatedAccount.id)
        assertEquals(newAccount.name, updatedAccount.name)
        assertEquals(newAccount.accountNumber, updatedAccount.accountNumber)
        assertEquals(newAccount.balance, updatedAccount.balance, 0.001)
        assertEquals(newAccount.profileImageUrl, updatedAccount.profileImageUrl)
    }

    @Test
    fun `updateUserAccount handles zero balance`() = runTest {
        // Given
        val zeroBalanceAccount = UserAccount(
            id = "USER_004",
            name = "Bob Zero",
            accountNumber = "0000000000",
            balance = 0.0,
            profileImageUrl = null
        )

        // When
        userRepository.updateUserAccount(zeroBalanceAccount)
        val updatedAccount = userRepository.getUserAccount().first()

        // Then
        assertEquals(0.0, updatedAccount.balance, 0.001)
    }

    @Test
    fun `deductBalance succeeds with sufficient balance`() = runTest {
        // Given
        val deductionAmount = 1000.0
        val initialBalance = userRepository.getUserAccount().first().balance

        // When
        val result = userRepository.deductBalance(deductionAmount)

        // Then
        assertTrue(result.isSuccess)
        val updatedAccount = result.getOrNull()
        assertNotNull(updatedAccount)
        assertEquals(initialBalance - deductionAmount, updatedAccount!!.balance, 0.001)

        // Verify the state is actually updated in the repository
        val repositoryAccount = userRepository.getUserAccount().first()
        assertEquals(updatedAccount.balance, repositoryAccount.balance, 0.001)
    }

    @Test
    fun `deductBalance fails with insufficient balance`() = runTest {
        // Given
        val currentAccount = userRepository.getUserAccount().first()
        val excessiveAmount = currentAccount.balance + 1000.0

        // When
        val result = userRepository.deductBalance(excessiveAmount)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("Insufficient balance"))
        assertTrue(exception.message!!.contains("Available: ${currentAccount.balance}"))
        assertTrue(exception.message!!.contains("Required: $excessiveAmount"))

        // Verify balance remains unchanged
        val unchangedAccount = userRepository.getUserAccount().first()
        assertEquals(currentAccount.balance, unchangedAccount.balance, 0.001)
    }

    @Test
    fun `deductBalance succeeds when deducting exact balance`() = runTest {
        // Given
        val currentAccount = userRepository.getUserAccount().first()
        val exactBalance = currentAccount.balance

        // When
        val result = userRepository.deductBalance(exactBalance)

        // Then
        assertTrue(result.isSuccess)
        val updatedAccount = result.getOrNull()
        assertNotNull(updatedAccount)
        assertEquals(0.0, updatedAccount!!.balance, 0.001)
    }

    @Test
    fun `repository maintains state consistency across operations`() = runTest {
        // Given
        val customAccount = UserAccount(
            id = "INTEGRATION_001",
            name = "Integration Test",
            accountNumber = "1122334455",
            balance = 1000.0,
            profileImageUrl = "https://example.com/test.jpg"
        )

        // When - Update account
        userRepository.updateUserAccount(customAccount)
        val afterUpdate = userRepository.getUserAccount().first()

        // Then - Verify update
        assertEquals(customAccount, afterUpdate)

        // When - Deduct balance
        val deductionResult = userRepository.deductBalance(300.0)

        // Then - Verify deduction
        assertTrue(deductionResult.isSuccess)
        val afterDeduction = userRepository.getUserAccount().first()
        assertEquals(700.0, afterDeduction.balance, 0.001)
    }
}