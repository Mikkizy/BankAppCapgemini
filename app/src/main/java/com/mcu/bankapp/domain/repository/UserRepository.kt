package com.mcu.bankapp.domain.repository

import com.mcu.bankapp.domain.models.UserAccount
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserAccount(): Flow<UserAccount>
    suspend fun updateUserAccount(userAccount: UserAccount)
    suspend fun deductBalance(amount: Double): Result<UserAccount>
}