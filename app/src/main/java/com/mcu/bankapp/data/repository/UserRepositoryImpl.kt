package com.mcu.bankapp.data.repository

import com.mcu.bankapp.domain.models.UserAccount
import com.mcu.bankapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val _userAccount = MutableStateFlow(
        UserAccount(
            id = "USER_001",
            name = "John Maxwell",
            accountNumber = "1234567890",
            balance = 5000.0,
            profileImageUrl = null
        )
    )

    override fun getUserAccount(): Flow<UserAccount> {
        return _userAccount.asStateFlow()
    }

    override suspend fun updateUserAccount(userAccount: UserAccount) {
        _userAccount.value = userAccount
    }

    override suspend fun deductBalance(amount: Double): Result<UserAccount> {
        val currentAccount = _userAccount.value
        return if (currentAccount.canTransfer(amount)) {
            val updatedAccount = currentAccount.deductAmount(amount)
            _userAccount.value = updatedAccount
            Result.success(updatedAccount)
        } else {
            Result.failure(Exception("Insufficient balance. Available: ${currentAccount.balance}, Required: $amount"))
        }
    }
}