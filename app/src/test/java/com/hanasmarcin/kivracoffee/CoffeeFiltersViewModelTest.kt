package com.hanasmarcin.kivracoffee

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hanasmarcin.kivracoffee.model.CoffeeFilters
import com.hanasmarcin.kivracoffee.model.FilterItem
import com.hanasmarcin.kivracoffee.model.FilterType
import com.hanasmarcin.kivracoffee.model.SortType
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeFiltersViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class CoffeeFiltersViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() // for synchronous execution

    private val testDispatcher = UnconfinedTestDispatcher() // for synchronous coroutines work

    private lateinit var viewModel: CoffeeFiltersViewModel
    private lateinit var emptyViewModel: CoffeeFiltersViewModel
    private lateinit var sampleFilterItemUnselected: FilterItem
    private lateinit var sampleFilterItemSelected: FilterItem
    private lateinit var sampleCoffeeFilters: CoffeeFilters

    @Before
    fun setup() {
        sampleFilterItemUnselected = FilterItem("TEST_NAME_1", 9, false)
        sampleFilterItemSelected = FilterItem("TEST_NAME_1", 9, true)
        sampleCoffeeFilters = CoffeeFilters(
            listOf(
                FilterItem("TEST_NAME_1", 9, false),
                FilterItem("TEST_NAME_2", 5, false),
                sampleFilterItemUnselected
            ),
            listOf(
                FilterItem("TEST_NAME_3", 1, true),
                FilterItem("TEST_NAME_4", 15, false),
                FilterItem("TEST_NAME_5", 56, false),
                FilterItem("TEST_NAME_6", 4, true)
            ),
            listOf(
                FilterItem("TEST_NAME_7", 5, false),
                FilterItem("TEST_NAME_8", 3, false),
                FilterItem("TEST_NAME_9", 14, true)
            ),
            listOf(
                FilterItem("TEST_NAME_10", 4, true),
                sampleFilterItemSelected
            ),
            SortType.BLEND_NAME
        )
        emptyViewModel = CoffeeFiltersViewModel(testDispatcher)
        viewModel = CoffeeFiltersViewModel(testDispatcher)
        viewModel.initFilters(sampleCoffeeFilters.copy())
    }

    @Test
    fun getFlagForCountryName_happyTest() {
        emptyViewModel.initFilters(sampleCoffeeFilters)
        Assert.assertEquals(sampleCoffeeFilters, emptyViewModel.coffeeFilters.value)
    }

    @Test
    fun selectSort_happyTest() {
        viewModel.selectSort(sortType = SortType.COUNTRY)
        Assert.assertEquals(SortType.COUNTRY, viewModel.coffeeFilters.value?.sortType)
    }

    @Test
    fun selectSort_failTest() {
        emptyViewModel.selectSort(sortType = SortType.COUNTRY)
        Assert.assertEquals(null, emptyViewModel.coffeeFilters.value?.sortType)
    }

    @Test
    fun changeFilterItemState_happyTest_select() {
        viewModel.changeFilterItemState(sampleFilterItemUnselected, FilterType.COUNTRY, true)
        Assert.assertTrue(viewModel.coffeeFilters.value?.countries?.any { it.name == sampleFilterItemUnselected.name && it.count == sampleFilterItemUnselected.count && it.isSelected }
            ?: false)
    }

    @Test
    fun changeFilterItemState_happyTest_unselect() {
        viewModel.changeFilterItemState(sampleFilterItemSelected, FilterType.INTENSIFIER, false)
        Assert.assertTrue(viewModel.coffeeFilters.value?.intensifiers?.any {
            it.name == sampleFilterItemSelected.name &&
                it.count == sampleFilterItemSelected.count &&
                !it.isSelected
        } ?: false)
    }

    @Test
    fun changeFilterItemState_failTest_wrongFilterType() {
        viewModel.changeFilterItemState(sampleFilterItemSelected, FilterType.NOTE, false)
        Assert.assertFalse(viewModel.coffeeFilters.value?.intensifiers?.any {
            it.name == sampleFilterItemSelected.name &&
                it.count == sampleFilterItemSelected.count &&
                !it.isSelected
        } ?: false)
    }

    @Test
    fun changeFilterItemState_failTest_unpopulated() {
        emptyViewModel.changeFilterItemState(
            sampleFilterItemSelected,
            FilterType.INTENSIFIER,
            false
        )
        Assert.assertFalse(viewModel.coffeeFilters.value?.intensifiers?.any {
            it.name == sampleFilterItemSelected.name &&
                it.count == sampleFilterItemSelected.count &&
                !it.isSelected
        } ?: false)
    }
}