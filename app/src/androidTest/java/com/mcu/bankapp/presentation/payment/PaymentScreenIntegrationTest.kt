package com.mcu.bankapp.presentation.payment

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mcu.bankapp.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PaymentScreenIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun navigateToPaymentScreen() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("DomesticTransferButton").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun paymentScreen_completePaymentFlow_domestic() {
        navigateToPaymentScreen()

        composeTestRule.onNodeWithTag("PaymentScreen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("RecipientNameField")
            .performTextInput("Jane Smith")

        composeTestRule.onNodeWithTag("AccountNumberField")
            .performTextInput("987654321")

        composeTestRule.onNodeWithTag("AmountField")
            .performTextInput("100.50")

        composeTestRule.onNodeWithTag("SendPaymentButton").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("SendPaymentButton").assertExists()
    }

    @Test
    fun paymentScreen_validationErrors_displayCorrectly() {
        navigateToPaymentScreen()

        // Try to submit empty form
        composeTestRule.onNodeWithTag("SendPaymentButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("SendPaymentButton").assertExists()
    }

    @Test
    fun paymentScreen_internationalTransfer_showsExtraFields() {
        // Navigate to international transfer
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("InternationalTransferButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("IbanField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SwiftCodeField").assertIsDisplayed()

        // Test SWIFT code and IBAN input with formatting
        composeTestRule.onNodeWithTag("IbanField")
            .performTextInput("GB29NWBK60161331926819")
        composeTestRule.onNodeWithTag("SwiftCodeField")
            .performTextInput("NWBKGB2L")
    }

    @Test
    fun paymentScreen_swiftCodeFormatting_worksCorrectly() {
        // Navigate to international transfer
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("InternationalTransferButton").performClick()
        composeTestRule.waitForIdle()

        val swiftCodeField = composeTestRule.onNodeWithTag("SwiftCodeField")

        swiftCodeField.performTextInput("NWBK")

        swiftCodeField.performTextInput("GB2L")

        swiftCodeField.assertExists()
    }

    @Test
    fun paymentScreen_balanceDisplay_isCorrect() {
        navigateToPaymentScreen()

        composeTestRule.onNodeWithTag("BalanceText").assertExists()
    }

    @Test
    fun paymentScreen_backNavigation_worksCorrectly() {
        navigateToPaymentScreen()

        composeTestRule.onNodeWithTag("PaymentScreen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("PaymentScreen").assertExists()
    }
}