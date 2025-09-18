package com.mcu.bankapp.domain.models

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class PaymentDataTest {

    @Test
    fun `isValidForDomestic returns true for valid domestic data`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50"
        )

        assertTrue(paymentData.isValidForDomestic())
    }

    @Test
    fun `isValidForDomestic returns false for invalid amount`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "invalid"
        )

        assertFalse(paymentData.isValidForDomestic())
    }

    @Test
    fun `isValidForDomestic returns false for negative amount`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "-10.0"
        )

        assertFalse(paymentData.isValidForDomestic())
    }

    @Test
    fun `isValidForInternational returns true for valid international data`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50",
            iban = "GB33BUKB20201555555555",
            swiftCode = "ABCD-EF-GH-12"
        )

        assertTrue(paymentData.isValidForInternational())
    }

    @Test
    fun `isValidForInternational returns false for invalid SWIFT code`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50",
            iban = "GB33BUKB20201555555555",
            swiftCode = "INVALID"
        )

        assertFalse(paymentData.isValidForInternational())
    }

    @Test
    fun `isValidForInternational returns false for IBAN exceeding 34 characters`() {
        val paymentData = PaymentData(
            recipientName = "John Doe",
            accountNumber = "123456789",
            amount = "100.50",
            iban = "GB33BUKB202015555555551234567890123",
            swiftCode = "ABCD-EF-GH-12"
        )

        assertFalse(paymentData.isValidForInternational())
    }
}