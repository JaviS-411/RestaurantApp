package com.example.testeableapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.testeableapp.model.MenuData
import org.junit.Rule
import org.junit.Test

class RestaurantOrderTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun emptyOrderMessageVisibleAtStart() {
        // Hacemos scroll hasta el mensaje para asegurar que sea visible en pantallas pequeñas
        composeTestRule.onNodeWithTag("emptyOrderMessage")
            .performScrollTo()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithTag("emptyOrderMessage")
            .assertTextEquals("El pedido está vacío. Añade productos del menú.")
    }

    @Test
    fun allMenuItemsVisible() {
        MenuData.items.forEach { item ->
            composeTestRule.onNodeWithTag("menuItem_${item.id}")
                .performScrollTo()
                .assertIsDisplayed()
            
            composeTestRule.onNodeWithTag("menuItemName_${item.id}")
                .assertTextEquals(item.name)
        }
    }

    @Test
    fun totalUpdatesWhenAddingItems() {
        val item1 = MenuData.items[0] // Patatas Bravas 5.50
        val item2 = MenuData.items[1] // Croquetas 6.00

        // Add first item
        composeTestRule.onNodeWithTag("addButton_${item1.id}")
            .performScrollTo()
            .performClick()

        // Total should be 5.50
        composeTestRule.onNodeWithTag("totalValue")
            .performScrollTo()
            .assertTextEquals("5.50 €")

        // Add second item
        composeTestRule.onNodeWithTag("addButton_${item2.id}")
            .performScrollTo()
            .performClick()

        // Total should be 11.50
        composeTestRule.onNodeWithTag("totalValue")
            .performScrollTo()
            .assertTextEquals("11.50 €")
    }

    // Additional UI Tests
    @Test
    fun itemAppearsInOrderListWhenAdded() {
        val item = MenuData.items[0]
        
        // Add item
        composeTestRule.onNodeWithTag("addButton_${item.id}")
            .performScrollTo()
            .performClick()
            
        // Check if it appears in "Tu Pedido" section
        composeTestRule.onNodeWithTag("orderItem_${item.id}")
            .performScrollTo()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithTag("orderItemName_${item.id}")
            .assertTextEquals(item.name)
    }

    @Test
    fun confirmationDialogShowsAfterPlacingOrder() {
        val item = MenuData.items[0]
        
        // Add item
        composeTestRule.onNodeWithTag("addButton_${item.id}")
            .performScrollTo()
            .performClick()
            
        // Place order
        composeTestRule.onNodeWithTag("placeOrderButton")
            .performScrollTo()
            .performClick()
            
        // Verify dialog
        composeTestRule.onNodeWithTag("confirmationDialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmationTitle").assertTextEquals("Pedido Confirmado")
        
        // Dismiss dialog
        composeTestRule.onNodeWithTag("confirmationOkButton").performClick()
        
        // Verify we are back to empty state
        composeTestRule.onNodeWithTag("emptyOrderMessage")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
