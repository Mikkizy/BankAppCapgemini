package com.mcu.bankapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAccount(
    val id: String = "USER_001",
    val name: String = "John Maxwell",
    val accountNumber: String = "1234567890",
    val balance: Double = 5000.0,
    val profileImageUrl: String? = null
) : Parcelable {

    fun canTransfer(amount: Double): Boolean {
        return amount > 0 && amount <= balance
    }

    fun deductAmount(amount: Double): UserAccount {
        return if (canTransfer(amount)) {
            copy(balance = balance - amount)
        } else {
            this
        }
    }
}