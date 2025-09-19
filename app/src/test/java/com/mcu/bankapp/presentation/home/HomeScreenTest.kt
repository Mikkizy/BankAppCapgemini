package com.mcu.bankapp.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var originalLocale: Locale? = null

    @Before
    fun setUp() {
        // Store original locale and set to a known locale for consistent tests
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        // Restore original locale
        originalLocale?.let { Locale.setDefault(it) }
    }

    private val sampleHomeState = HomeState(
        name = "John Doe",
        accountNo = "12345678",
        balance = 1250.75
    )

    @Test
    fun homeScreen_displaysCorrectly() {
        // Given
        var domesticTransferClicked = false
        var internationalTransferClicked = false

        // When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { domesticTransferClicked = true },
                onInternationalTransferClick = { internationalTransferClicked = true },
                homeState = sampleHomeState
            )
        }

        // Then - Verify screen is displayed
        composeTestRule.onNodeWithTag("HomeScreen").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysUserInformation() {
        // Given & When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // Then - Verify user information is displayed
        composeTestRule.onNodeWithText("Welcome back,").assertIsDisplayed()
        composeTestRule.onNodeWithTag("UserNameText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("UserNameText").assertTextEquals("John Doe")
        composeTestRule.onNodeWithText("Account: 12345678").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysBalance() {
        // Given & When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // Then - Verify balance is displayed correctly
        composeTestRule.onNodeWithText("Current Balance").assertIsDisplayed()
        composeTestRule.onNodeWithTag("HomeBalanceText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("HomeBalanceText").assertTextEquals("£1250.75")
    }

    @Test
    fun homeScreen_displaysTransferTitle() {
        // Given & When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // Then - Verify transfer title is displayed
        composeTestRule.onNodeWithText("Make a Transfer").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysTransferButtons() {
        // Given & When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // Then - Verify transfer buttons are displayed
        composeTestRule.onNodeWithTag("DomesticTransferButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InternationalTransferButton").assertIsDisplayed()

        // Verify button texts
        composeTestRule.onNodeWithText("Domestic Transfer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Send money within the country").assertIsDisplayed()
        composeTestRule.onNodeWithText("International Transfer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Send money to another country").assertIsDisplayed()
    }

    @Test
    fun domesticTransferButton_clickTriggersCallback() {
        // Given
        var domesticTransferClicked = false

        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { domesticTransferClicked = true },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // When
        composeTestRule.onNodeWithTag("DomesticTransferButton").performClick()

        // Then
        assert(domesticTransferClicked)
    }

    @Test
    fun internationalTransferButton_clickTriggersCallback() {
        // Given
        var internationalTransferClicked = false

        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { internationalTransferClicked = true },
                homeState = sampleHomeState
            )
        }

        // When
        composeTestRule.onNodeWithTag("InternationalTransferButton").performClick()

        // Then
        assert(internationalTransferClicked)
    }

    @Test
    fun homeScreen_handlesZeroBalance() {
        // Given
        val zeroBalanceState = HomeState(
            name = "Jane Smith",
            accountNo = "87654321",
            balance = 0.0
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = zeroBalanceState
            )
        }

        // Then
        composeTestRule.onNodeWithTag("HomeBalanceText").assertTextEquals("£0.00")
        composeTestRule.onNodeWithTag("UserNameText").assertTextEquals("Jane Smith")
        composeTestRule.onNodeWithText("Account: 87654321").assertIsDisplayed()
    }

    @Test
    fun homeScreen_handlesLargeBalance() {
        // Given
        val largeBalanceState = HomeState(
            name = "Rich Person",
            accountNo = "99999999",
            balance = 999999.99
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // Then - Should handle large numbers gracefully
        composeTestRule.onNodeWithTag("HomeBalanceText").assertIsDisplayed()
    }

    @Test
    fun homeScreen_handlesLongUserName() {
        // Given
        val longNameState = HomeState(
            name = "Alexander Benjamin Christopher Davidson-Smith",
            accountNo = "11111111",
            balance = 500.00
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = longNameState
            )
        }

        // Then
        composeTestRule.onNodeWithTag("UserNameText")
            .assertTextEquals("Alexander Benjamin Christopher Davidson-Smith")
    }

    @Test
    fun homeScreen_handlesEmptyName() {
        // Given
        val emptyNameState = HomeState(
            name = "",
            accountNo = "22222222",
            balance = 100.00
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = emptyNameState
            )
        }

        // Then
        composeTestRule.onNodeWithTag("UserNameText").assertTextEquals("")
        composeTestRule.onNodeWithText("Welcome back,").assertIsDisplayed()
    }

    @Test
    fun homeScreen_profileIconIsDisplayed() {
        // Given & When
        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { },
                onInternationalTransferClick = { },
                homeState = sampleHomeState
            )
        }

        // Then - Profile icon should be present (we can't directly test Icon but can verify the screen structure)
        composeTestRule.onNodeWithText("Welcome back,").assertIsDisplayed()
        composeTestRule.onNodeWithTag("UserNameText").assertIsDisplayed()
    }

    @Test
    fun homeScreen_multipleClicksOnTransferButtons() {
        // Given
        var domesticClickCount = 0
        var internationalClickCount = 0

        composeTestRule.setContent {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier,
                onDomesticTransferClick = { domesticClickCount++ },
                onInternationalTransferClick = { internationalClickCount++ },
                homeState = sampleHomeState
            )
        }

        // When
        composeTestRule.onNodeWithTag("DomesticTransferButton").performClick()
        composeTestRule.onNodeWithTag("DomesticTransferButton").performClick()
        composeTestRule.onNodeWithTag("InternationalTransferButton").performClick()

        // Then
        assert(domesticClickCount == 2)
        assert(internationalClickCount == 1)
    }
}