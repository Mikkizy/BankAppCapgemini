package com.mcu.bankapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentData(
    val recipientName: String = "",
    val accountNumber: String = "",
    val amount: String = "",
    val iban: String = "",
    val swiftCode: String = ""
) : Parcelable {

    fun isValidForDomestic(): Boolean {
        return recipientName.isNotBlank() &&
                accountNumber.isNotBlank() &&
                amount.isNotBlank() &&
                amount.toDoubleOrNull() != null &&
                amount.toDouble() > 0
    }

    fun isValidForInternational(): Boolean {
        return isValidForDomestic() &&
                iban.isNotBlank() &&
                iban.length <= 34 &&
                swiftCode.isNotBlank() &&
                isValidSwiftCode(swiftCode)
    }

    private fun isValidSwiftCode(swiftCode: String): Boolean {
        // SWIFT code format: AAAA-BB-CC-12 (4 letters, 2 letters, 2 letters/digits, 2 digits)
        val swiftPattern = "^[A-Z]{4}-[A-Z]{2}-[A-Z0-9]{2}-[0-9]{2}$".toRegex()
        return swiftPattern.matches(swiftCode)
    }


}
