package com.mcu.bankapp.domain.usecases

import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class ValidatePaymentUseCaseTest {

    private lateinit var validatePaymentUseCase: ValidatePaymentUseCase

    @Before
    fun setUp() {
        validatePaymentUseCase = ValidatePaymentUseCase()
    }

    @Test
    fun `execute returns success for valid domestic transfer`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50"
        )

        val result = validatePaymentUseCase.execute(paymentData, TransferType.DOMESTIC)

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `execute returns error for missing recipient name in domestic transfer`() {
        val paymentData = PaymentData(
            recipientName = "",
            accountNumber = "123456789",
            amount = "100.50"
        )

        val result = validatePaymentUseCase.execute(paymentData, TransferType.DOMESTIC)

        assertTrue(result is ValidationResult.Error)
        val errorResult = result as ValidationResult.Error
        assertTrue(errorResult.errors.contains("Recipient name is required"))
    }

    @Test
    fun `execute returns success for valid international transfer`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50",
            iban = "GB33BUKB20201555555555",
            swiftCode = "ABCD-EF-GH-12"
        )

        val result = validatePaymentUseCase.execute(paymentData, TransferType.INTERNATIONAL)

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `execute returns error for invalid SWIFT code in international transfer`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50",
            iban = "GB33BUKB20201555555555",
            swiftCode = "INVALID"
        )

        val result = validatePaymentUseCase.execute(paymentData, TransferType.INTERNATIONAL)

        assertTrue(result is ValidationResult.Error)
        val errorResult = result as ValidationResult.Error
        assertTrue(errorResult.errors.any { it.contains("SWIFT code") })
    }
}