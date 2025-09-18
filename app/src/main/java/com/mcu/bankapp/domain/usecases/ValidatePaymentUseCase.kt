package com.mcu.bankapp.domain.usecases

import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import javax.inject.Inject

class ValidatePaymentUseCase @Inject constructor() {

    fun execute(paymentData: PaymentData, transferType: TransferType): ValidationResult {
        return when (transferType) {
            TransferType.DOMESTIC -> validateDomesticTransfer(paymentData)
            TransferType.INTERNATIONAL -> validateInternationalTransfer(paymentData)
        }
    }

    private fun validateDomesticTransfer(paymentData: PaymentData): ValidationResult {
        val errors = mutableListOf<String>()

        if (paymentData.recipientName.isBlank()) {
            errors.add("Recipient name is required")
        }

        if (paymentData.accountNumber.isBlank()) {
            errors.add("Account number is required")
        }

        if (paymentData.amount.isBlank()) {
            errors.add("Amount is required")
        } else {
            val amountValue = paymentData.amount.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0) {
                errors.add("Invalid amount")
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    private fun validateInternationalTransfer(paymentData: PaymentData): ValidationResult {
        val domesticResult = validateDomesticTransfer(paymentData)
        if (domesticResult is ValidationResult.Error) {
            return domesticResult
        }

        val errors = mutableListOf<String>()

        if (paymentData.iban.isBlank()) {
            errors.add("IBAN is required")
        } else if (paymentData.iban.length > 34) {
            errors.add("IBAN must not exceed 34 characters")
        }

        if (paymentData.swiftCode.isBlank()) {
            errors.add("SWIFT code is required")
        } else if (!isValidSwiftCode(paymentData.swiftCode)) {
            errors.add("Invalid SWIFT code format (should be AAAA-BB-CC-12)")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    private fun isValidSwiftCode(swiftCode: String): Boolean {
        val swiftPattern = "^[A-Z]{4}-[A-Z]{2}-[A-Z0-9]{2}-[0-9]{2}$".toRegex()
        return swiftPattern.matches(swiftCode)
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val errors: List<String>) : ValidationResult()
}