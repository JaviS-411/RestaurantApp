package com.example.testeableapp

import com.example.testeableapp.model.MenuData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantViewModelTest {

    private lateinit var viewModel: RestaurantViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RestaurantViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addItem should add item to quantities`() {
        val itemId = MenuData.items[0].id
        viewModel.addItem(itemId)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val quantities = viewModel.quantities.value
        assertEquals(1, quantities[itemId])
        assertFalse(viewModel.isEmpty.value)
    }

    @Test
    fun `increment and decrement item quantity`() {
        val itemId = MenuData.items[0].id
        viewModel.addItem(itemId) // Qty 1
        viewModel.incrementItem(itemId) // Qty 2
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(2, viewModel.quantities.value[itemId])
        
        viewModel.decrementItem(itemId) // Qty 1
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.quantities.value[itemId])
    }

    @Test
    fun `decrementing item from 1 should remove it from order`() {
        val itemId = MenuData.items[0].id
        viewModel.addItem(itemId)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.quantities.value[itemId])
        
        viewModel.decrementItem(itemId)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.quantities.value[itemId])
        assertTrue(viewModel.isEmpty.value)
    }

    @Test
    fun `calculate total should sum prices correctly`() {
        val item1 = MenuData.items[0] // Patatas Bravas 5.50
        val item2 = MenuData.items[1] // Croquetas 6.00
        
        viewModel.addItem(item1.id)
        viewModel.addItem(item2.id)
        viewModel.incrementItem(item2.id) // 1 * 5.50 + 2 * 6.00 = 17.50
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(17.50, viewModel.total.value, 0.01)
    }

    // Additional Unit Tests
    @Test
    fun `placeOrder should set confirmation state`() {
        val item = MenuData.items[0]
        viewModel.addItem(item.id)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val confirmation = viewModel.confirmation.value
        assertNotNull(confirmation)
        assertEquals(1, confirmation?.itemCount)
        assertEquals(item.price, confirmation?.total ?: 0.0, 0.01)
    }

    @Test
    fun `dismissConfirmation should clear order and confirmation`() {
        viewModel.addItem(MenuData.items[0].id)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.dismissConfirmation()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNull(viewModel.confirmation.value)
        assertTrue(viewModel.quantities.value.isEmpty())
        assertTrue(viewModel.isEmpty.value)
    }

    @Test
    fun addingSameItemMultipleTimesIncrementsQuantity() {
        val itemId = MenuData.items[0].id

        viewModel.addItem(itemId)
        viewModel.addItem(itemId)
        viewModel.addItem(itemId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.quantities.value[itemId])
    }

    @Test
    fun totalResetsToZeroWhenAllItemsRemoved() {
        val item = MenuData.items[0]

        viewModel.addItem(item.id)
        viewModel.incrementItem(item.id)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.decrementItem(item.id)
        viewModel.decrementItem(item.id)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0.0, viewModel.total.value, 0.01)
    }


}
