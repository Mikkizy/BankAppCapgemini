package com.mcu.bankapp.utils

import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.models.UserAccount
import com.mcu.bankapp.presentation.home.HomeState
import com.mcu.bankapp.presentation.payment.PaymentState

object TestUtils {

    fun createMockUserAccount(
        accountNo: String = "123456789",
        balance: Double = 5000.0,
        name: String = "John Doe"
    ) = UserAccount(
        accountNumber = accountNo,
        balance = balance,
        name = name
    )

    fun createMockHomeState(
        balance: Double = 5000.0,
        name: String = "John Doe",
        accountNo: String = "123456789",
        profileUrl: String = ""
    ) = HomeState(
        balance = balance,
        name = name,
        accountNo = accountNo,
        profileUrl = profileUrl
    )

    fun createMockPaymentState(
        transferType: TransferType = TransferType.DOMESTIC,
        paymentData: PaymentData = PaymentData(),
        userAccount: UserAccount = createMockUserAccount(),
        isLoading: Boolean = false,
        validationErrors: List<String> = emptyList(),
        paymentResult: com.mcu.bankapp.domain.usecases.PaymentResult? = null
    ) = PaymentState(
        transferType = transferType,
        paymentData = paymentData,
        userAccount = userAccount,
        isLoading = isLoading,
        validationErrors = validationErrors,
        paymentResult = paymentResult
    )

    fun createMockPaymentData(
        recipientName: String = "",
        accountNumber: String = "",
        amount: String = "",
        iban: String = "",
        swiftCode: String = ""
    ) = PaymentData(
        recipientName = recipientName,
        accountNumber = accountNumber,
        amount = amount,
        iban = iban,
        swiftCode = swiftCode
    )
}