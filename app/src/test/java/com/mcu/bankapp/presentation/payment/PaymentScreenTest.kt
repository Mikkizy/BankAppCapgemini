package com.mcu.bankapp.presentation.payment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mcu.bankapp.domain.models.PaymentData
import com.mcu.bankapp.domain.models.TransferType
import com.mcu.bankapp.domain.models.UserAccount
import com.mcu.bankapp.domain.usecases.PaymentResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class PaymentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var originalLocale: Locale? = null

    private val sampleUserAccount = UserAccount(
        accountNumber = "12345678",
        balance = 5000.0,
        name = "John Doe"
    )

    private val samplePaymentData = PaymentData(
        recipientName = "",
        accountNumber = "",
        amount = "",
        iban = "",
        swiftCode = ""
    )

    private val samplePaymentState = PaymentState(
        paymentData = samplePaymentData,
        userAccount = sampleUserAccount,
        transferType = TransferType.DOMESTIC,
        isLoading = false,
        validationErrors = emptyList(),
        paymentResult = null
    )

    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        originalLocale?.let { Locale.setDefault(it) }
    }

    @Test
    fun paymentScreen_displaysCorrectly_forDomesticTransfer() {
        // Given & When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("PaymentScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ScreenTitle").assertTextEquals("Domestic Transfer")
        composeTestRule.onNodeWithText("Available Balance").assertIsDisplayed()
        composeTestRule.onNodeWithTag("BalanceText").assertTextEquals("£5000.00")
    }

    @Test
    fun paymentScreen_displaysCorrectly_forInternationalTransfer() {
        // Given
        val internationalState = samplePaymentState.copy(
            transferType = TransferType.INTERNATIONAL
        )

        // When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.INTERNATIONAL,
                navigateToHome = { },
                uiState = internationalState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("ScreenTitle").assertTextEquals("International Transfer")

        // Wait for UI to settle after LaunchedEffect
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("IbanField").assertExists()
        composeTestRule.onNodeWithTag("SwiftCodeField").assertExists()
    }

    @Test
    fun paymentScreen_displaysCommonFields() {
        // Given & When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then - Verify common fields are present
        composeTestRule.onNodeWithTag("RecipientNameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AccountNumberField").assertExists()
        composeTestRule.onNodeWithTag("AmountField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SendPaymentButton").assertExists()
    }

    @Test
    fun paymentScreen_hidesInternationalFields_forDomesticTransfer() {
        // Given & When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then - International fields should not be displayed
        composeTestRule.onNodeWithTag("IbanField").assertDoesNotExist()
        composeTestRule.onNodeWithTag("SwiftCodeField").assertDoesNotExist()
    }

    @Test
    fun recipientNameField_updatesPaymentData() {
        // Given
        var updatedPaymentData: PaymentData? = null

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { updatedPaymentData = it },
                processPayment = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag("RecipientNameField").performTextInput("Jane Smith")

        // Then
        assert(updatedPaymentData?.recipientName == "Jane Smith")
    }

    @Test
    fun accountNumberField_updatesPaymentData() {
        // Given
        var updatedPaymentData: PaymentData? = null

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { updatedPaymentData = it },
                processPayment = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag("AccountNumberField").performTextInput("87654321")

        // Then
        assert(updatedPaymentData?.accountNumber == "87654321")
    }

    @Test
    fun amountField_updatesPaymentData() {
        // Given
        var updatedPaymentData: PaymentData? = null

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { updatedPaymentData = it },
                processPayment = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag("AmountField").performTextInput("150.50")

        // Then
        assert(updatedPaymentData?.amount == "150.50")
    }

    @Test
    fun ibanField_updatesPaymentData_andConvertsToUppercase() {
        // Given
        var updatedPaymentData: PaymentData? = null
        val internationalState = samplePaymentState.copy(
            transferType = TransferType.INTERNATIONAL
        )

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.INTERNATIONAL,
                navigateToHome = { },
                uiState = internationalState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { updatedPaymentData = it },
                processPayment = { }
            )
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // When
        composeTestRule.onNodeWithTag("IbanField").performTextInput("gb33bukb20201555555555")

        // Then
        assert(updatedPaymentData?.iban == "GB33BUKB20201555555555")
    }

    @Test
    fun ibanField_limitsTo34Characters() {
        // Given
        var updatedPaymentData: PaymentData? = null
        val internationalState = samplePaymentState.copy(
            transferType = TransferType.INTERNATIONAL
        )

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.INTERNATIONAL,
                navigateToHome = { },
                uiState = internationalState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { updatedPaymentData = it },
                processPayment = { }
            )
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // When - Try to input more than 34 characters
        val longIban = "GB33BUKB20201555555555EXTRALONGPART"
        composeTestRule.onNodeWithTag("IbanField").performTextClearance()
        composeTestRule.onNodeWithTag("IbanField").performTextInput(longIban)

        // Then - Should be limited to 34 characters
        assert((updatedPaymentData?.iban?.length ?: 0) <= 34)
    }

    @Test
    fun swiftCodeField_formatsCorrectly() {
        // Given
        var updatedPaymentData: PaymentData? = null
        val internationalState = samplePaymentState.copy(
            transferType = TransferType.INTERNATIONAL
        )

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.INTERNATIONAL,
                navigateToHome = { },
                uiState = internationalState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { updatedPaymentData = it },
                processPayment = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag("SwiftCodeField").performTextInput("ABCDEFGH12")

        // Then - Should be formatted with dashes
        assert(updatedPaymentData?.swiftCode == "ABCD-EF-GH-12")
    }

    @Test
    fun sendPaymentButton_showsLoadingState() {
        // Given
        val loadingState = samplePaymentState.copy(isLoading = true)

        // When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = loadingState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("SendPaymentButton").assertIsNotEnabled()
    }

    @Test
    fun errorCard_hiddenWhenNoErrors() {

        // When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("ErrorCard").assertDoesNotExist()
    }

    @Test
    fun paymentResultDialog_displaysOnError() {
        // Given
        val errorState = samplePaymentState.copy(
            paymentResult = PaymentResult.Error("Insufficient funds")
        )

        // When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = errorState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("PaymentResultDialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Payment Failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Insufficient funds").assertIsDisplayed()
    }

    @Test
    fun paymentResultDialog_callsClearPaymentResult_onDismiss() {
        // Given
        var clearPaymentResultCalled = false
        val successState = samplePaymentState.copy(
            paymentResult = PaymentResult.Success("TXN123456")
        )

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = successState,
                updateTransferType = { },
                clearPaymentResult = { clearPaymentResultCalled = true },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag("DialogConfirmButton").performClick()

        // Then
        assert(clearPaymentResultCalled)
    }

    @Test
    fun paymentResultDialog_callsResetFormAndNavigateToHome_onSuccessConfirm() {
        // Given
        var resetFormCalled = false
        var navigateToHomeCalled = false
        val successState = samplePaymentState.copy(
            paymentResult = PaymentResult.Success("TXN123456")
        )

        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { navigateToHomeCalled = true },
                uiState = successState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { resetFormCalled = true },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // When
        composeTestRule.onNodeWithTag("DialogConfirmButton").performClick()

        // Then
        assert(resetFormCalled)
        assert(navigateToHomeCalled)
    }

    @Test
    fun updateTransferType_calledOnLaunch() {
        // Given
        var updatedTransferType: TransferType? = null

        // When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.INTERNATIONAL,
                navigateToHome = { },
                uiState = samplePaymentState,
                updateTransferType = { updatedTransferType = it },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        assert(updatedTransferType == TransferType.INTERNATIONAL)
    }

    @Test
    fun paymentScreen_handlesLowBalance() {
        // Given
        val lowBalanceAccount = sampleUserAccount.copy(balance = 10.50)
        val lowBalanceState = samplePaymentState.copy(userAccount = lowBalanceAccount)

        // When
        composeTestRule.setContent {
            PaymentScreen(
                transferType = TransferType.DOMESTIC,
                navigateToHome = { },
                uiState = lowBalanceState,
                updateTransferType = { },
                clearPaymentResult = { },
                resetForm = { },
                updatePaymentData = { },
                processPayment = { }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("BalanceText").assertTextEquals("£10.50")
    }
}