package com.mcu.bankapp.presentation.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mcu.bankapp.domain.models.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.*
import com.mcu.bankapp.domain.repository.UserRepository

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockUserRepository: UserRepository
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var testDispatcher: TestDispatcher

    // Test data
    private val testUserAccount = UserAccount(
        id = "TEST_001",
        name = "John Doe",
        accountNumber = "1234567890",
        balance = 5000.0,
        profileImageUrl = "https://example.com/profile.jpg"
    )

    private val updatedUserAccount = UserAccount(
        id = "TEST_002",
        name = "Jane Smith",
        accountNumber = "0987654321",
        balance = 7500.0,
        profileImageUrl = "https://example.com/jane.jpg"
    )

    private val userAccountWithNullProfile = UserAccount(
        id = "TEST_003",
        name = "Bob Johnson",
        accountNumber = "1111222233",
        balance = 3000.0,
        profileImageUrl = null
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockUserRepository = mockk()
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `homeState should have initial empty state`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)

        // Then - Check initial state before collection
        val initialState = homeViewModel.homeState.first()
        assertEquals(HomeState(), initialState)
    }

    @Test
    fun `userAccount should be exposed from repository`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)

        // Then
        val exposedUserAccount = homeViewModel.userAccount.first()
        assertEquals(testUserAccount, exposedUserAccount)
    }

    @Test
    fun `homeState should update when userAccount emits new data`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle() // Allow init block to complete

        // Then
        val updatedState = homeViewModel.homeState.first()
        assertEquals(testUserAccount.balance, updatedState.balance, 0.001)
        assertEquals(testUserAccount.name, updatedState.name)
        assertEquals(testUserAccount.accountNumber, updatedState.accountNo)
        assertEquals(testUserAccount.profileImageUrl.toString(), updatedState.profileUrl)
    }

    @Test
    fun `homeState should handle null profileImageUrl correctly`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(userAccountWithNullProfile)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.homeState.first()
        assertEquals("null", state.profileUrl) // null.toString() returns "null"
        assertEquals(userAccountWithNullProfile.name, state.name)
        assertEquals(userAccountWithNullProfile.balance, state.balance, 0.001)
        assertEquals(userAccountWithNullProfile.accountNumber, state.accountNo)
    }

    @Test
    fun `homeState should update when userAccount flow emits updated data`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Update the flow with new data
        userAccountFlow.value = updatedUserAccount
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val finalState = homeViewModel.homeState.first()
        assertEquals(updatedUserAccount.balance, finalState.balance, 0.001)
        assertEquals(updatedUserAccount.name, finalState.name)
        assertEquals(updatedUserAccount.accountNumber, finalState.accountNo)
        assertEquals(updatedUserAccount.profileImageUrl.toString(), finalState.profileUrl)
    }

    @Test
    fun `homeState should reflect multiple account updates`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify initial state
        var currentState = homeViewModel.homeState.first()
        assertEquals(testUserAccount.balance, currentState.balance, 0.001)
        assertEquals(testUserAccount.name, currentState.name)

        // When - First update
        userAccountFlow.value = updatedUserAccount
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Verify first update
        currentState = homeViewModel.homeState.first()
        assertEquals(updatedUserAccount.balance, currentState.balance, 0.001)
        assertEquals(updatedUserAccount.name, currentState.name)

        // When - Second update
        userAccountFlow.value = userAccountWithNullProfile
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Verify second update
        currentState = homeViewModel.homeState.first()
        assertEquals(userAccountWithNullProfile.balance, currentState.balance, 0.001)
        assertEquals(userAccountWithNullProfile.name, currentState.name)
        assertEquals("null", currentState.profileUrl)
    }

    @Test
    fun `homeState should handle zero balance correctly`() = runTest {
        // Given
        val zeroBalanceAccount = UserAccount(
            id = "ZERO_001",
            name = "Zero Balance User",
            accountNumber = "0000000000",
            balance = 0.0,
            profileImageUrl = "https://example.com/zero.jpg"
        )
        val userAccountFlow = MutableStateFlow(zeroBalanceAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.homeState.first()
        assertEquals(0.0, state.balance, 0.001)
        assertEquals(zeroBalanceAccount.name, state.name)
        assertEquals(zeroBalanceAccount.accountNumber, state.accountNo)
    }

    @Test
    fun `homeState should handle negative balance correctly`() = runTest {
        // Given
        val negativeBalanceAccount = UserAccount(
            id = "NEG_001",
            name = "Negative Balance User",
            accountNumber = "9999999999",
            balance = -500.0,
            profileImageUrl = null
        )
        val userAccountFlow = MutableStateFlow(negativeBalanceAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.homeState.first()
        assertEquals(-500.0, state.balance, 0.001)
        assertEquals(negativeBalanceAccount.name, state.name)
        assertEquals("null", state.profileUrl)
    }

    @Test
    fun `homeState should handle decimal balance values correctly`() = runTest {
        // Given
        val decimalBalanceAccount = UserAccount(
            id = "DEC_001",
            name = "Decimal User",
            accountNumber = "1234567890",
            balance = 1234.56,
            profileImageUrl = "https://example.com/decimal.jpg"
        )
        val userAccountFlow = MutableStateFlow(decimalBalanceAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.homeState.first()
        assertEquals(1234.56, state.balance, 0.001)
        assertEquals(decimalBalanceAccount.name, state.name)
    }

    @Test
    fun `homeState should handle empty string values correctly`() = runTest {
        // Given
        val emptyStringAccount = UserAccount(
            id = "",
            name = "",
            accountNumber = "",
            balance = 1000.0,
            profileImageUrl = ""
        )
        val userAccountFlow = MutableStateFlow(emptyStringAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.homeState.first()
        assertEquals("", state.name)
        assertEquals("", state.accountNo)
        assertEquals("", state.profileUrl)
        assertEquals(1000.0, state.balance, 0.001)
    }

    @Test
    fun `homeState should maintain state immutability on updates`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val stateBeforeUpdate = homeViewModel.homeState.first()
        userAccountFlow.value = updatedUserAccount
        testDispatcher.scheduler.advanceUntilIdle()
        val stateAfterUpdate = homeViewModel.homeState.first()

        // Then - Verify states are different instances
        assertNotSame(stateBeforeUpdate, stateAfterUpdate)

        // Verify old state remains unchanged
        assertEquals(testUserAccount.balance, stateBeforeUpdate.balance, 0.001)
        assertEquals(testUserAccount.name, stateBeforeUpdate.name)

        // Verify new state has updated values
        assertEquals(updatedUserAccount.balance, stateAfterUpdate.balance, 0.001)
        assertEquals(updatedUserAccount.name, stateAfterUpdate.name)
    }

    @Test
    fun `viewModel should handle rapid consecutive updates correctly`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Rapid updates
        userAccountFlow.value = updatedUserAccount
        userAccountFlow.value = userAccountWithNullProfile
        userAccountFlow.value = testUserAccount
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Should reflect the final state
        val finalState = homeViewModel.homeState.first()
        assertEquals(testUserAccount.balance, finalState.balance, 0.001)
        assertEquals(testUserAccount.name, finalState.name)
        assertEquals(testUserAccount.accountNumber, finalState.accountNo)
        assertEquals(testUserAccount.profileImageUrl.toString(), finalState.profileUrl)
    }

    @Test
    fun `should verify getUserAccount is called during initialization`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)

        // Then
        verify(exactly = 1) { mockUserRepository.getUserAccount() }
    }

    @Test
    fun `should not call any other repository methods during initialization`() = runTest {
        // Given
        val userAccountFlow = MutableStateFlow(testUserAccount)
        every { mockUserRepository.getUserAccount() } returns userAccountFlow

        // When
        homeViewModel = HomeViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(exactly = 1) { mockUserRepository.getUserAccount() }
        confirmVerified(mockUserRepository)
    }
}