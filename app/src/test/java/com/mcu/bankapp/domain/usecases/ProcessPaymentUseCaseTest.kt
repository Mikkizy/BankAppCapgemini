package com.mcu.bankapp.domain.usecases

import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class ProcessPaymentUseCaseTest {

    private lateinit var processPaymentUseCase: ProcessPaymentUseCase

    @Before
    fun setUp() {
        processPaymentUseCase = ProcessPaymentUseCase()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execute should return Success with generated transaction ID when processing completes successfully`() = runTest {
        val timestampBefore = System.currentTimeMillis()

        val result = processPaymentUseCase.execute()

        val timestampAfter = System.currentTimeMillis()

        assertTrue(result is PaymentResult.Success)
        assertTrue(result.transactionId.startsWith("TXN"))

        val transactionTimestamp = result.transactionId.removePrefix("TXN").toLong()
        assertTrue(transactionTimestamp >= timestampBefore)
        assertTrue(transactionTimestamp <= timestampAfter)
    }

    @Test
    fun `execute should complete within reasonable time frame`() = runTest {
        val startTime = System.currentTimeMillis()

        val result = processPaymentUseCase.execute()

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Execution time: $executionTime ms")

        assertTrue(result is PaymentResult.Success)
        assertTrue(executionTime <= 1000)
    }

    @Test
    fun `execute should generate unique transaction IDs for consecutive calls`() = runTest {
        val result1 = processPaymentUseCase.execute()
        val result2 = processPaymentUseCase.execute()

        assertTrue(result1 is PaymentResult.Success)
        assertTrue(result2 is PaymentResult.Success)
        assertTrue(result1.transactionId != result2.transactionId)
    }

    @Test
    fun `generateTransactionId should create valid transaction ID format`() {
        val useCase = ProcessPaymentUseCase()

        val method = ProcessPaymentUseCase::class.java.getDeclaredMethod("generateTransactionId")
        method.isAccessible = true
        val transactionId = method.invoke(useCase) as String

        assertTrue(transactionId.startsWith("TXN"))
        assertTrue(transactionId.length > 3) // "TXN" + timestamp

        val timestampPart = transactionId.removePrefix("TXN")
        assertTrue(timestampPart.all { it.isDigit() })
    }
}