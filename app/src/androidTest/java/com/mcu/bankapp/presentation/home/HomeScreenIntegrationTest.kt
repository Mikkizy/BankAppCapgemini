package com.mcu.bankapp.presentation.home

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
class HomeScreenIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_navigatesToDomesticPayment() {
        // Wait for home screen to load
        composeTestRule.waitForIdle()

        // Verify home screen is displayed
        composeTestRule.onNodeWithTag("HomeScreen").assertIsDisplayed()

        // Click domestic transfer button
        composeTestRule.onNodeWithTag("DomesticTransferButton").performClick()

        // Verify navigation to payment screen
        composeTestRule.onNodeWithTag("PaymentScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ScreenTitle").assertTextEquals("Domestic Transfer")
    }

    @Test
    fun homeScreen_navigatesToInternationalPayment() {
        // Wait for home screen to load
        composeTestRule.waitForIdle()

        // Verify home screen is displayed
        composeTestRule.onNodeWithTag("HomeScreen").assertIsDisplayed()

        // Click international transfer button
        composeTestRule.onNodeWithTag("InternationalTransferButton").performClick()

        // Verify navigation to payment screen
        composeTestRule.onNodeWithTag("PaymentScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ScreenTitle").assertTextEquals("International Transfer")

        // Verify international-specific fields are present
        composeTestRule.onNodeWithTag("IbanField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SwiftCodeField").assertIsDisplayed()
    }

    @Test
    fun homeScreen_buttonsAreClickable() {
        // Wait for home screen to load
        composeTestRule.waitForIdle()

        // Verify buttons are enabled and clickable
        composeTestRule.onNodeWithTag("DomesticTransferButton")
            .assertIsEnabled()
            .assertHasClickAction()

        composeTestRule.onNodeWithTag("InternationalTransferButton")
            .assertIsEnabled()
            .assertHasClickAction()
    }
}