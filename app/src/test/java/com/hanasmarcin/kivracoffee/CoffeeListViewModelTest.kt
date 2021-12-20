package com.hanasmarcin.kivracoffee

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hanasmarcin.kivracoffee.model.CoffeeModel
import com.hanasmarcin.kivracoffee.model.FilterItem
import com.hanasmarcin.kivracoffee.model.repository.CoffeeRepository
import com.hanasmarcin.kivracoffee.model.repository.CountryRepository
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeFiltersViewModel
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CoffeeListViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() // for synchronous execution

    private val testDispatcher = UnconfinedTestDispatcher() // for synchronous coroutines work

    @Mock
    private val coffeeRepository: CoffeeRepository = mock(CoffeeRepository::class.java)

    @Mock
    private val countryRepository: CountryRepository = mock(CountryRepository::class.java)

    @Mock
    private val sampleBitmap: BitmapDrawable = mock(BitmapDrawable::class.java)

    init {
        `when`(countryRepository.getFromName(anyString())).thenReturn(sampleBitmap)
    }

    private lateinit var viewModel: CoffeeListViewModel
    private lateinit var sampleCoffeeList: List<CoffeeModel>

    @Before
    fun setup() {
        sampleCoffeeList = listOf(
            CoffeeModel(
                "TEST_UID1",
                1111232,
                "TEST_BLEND_1",
                "TEST_CITY_1",
                "TEST_COUNTRY_1",
                "TEST_VARIETY_1",
                listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_3"),
                "TEST_INTENSIFIER_1"
            ),
            CoffeeModel(
                "TEST_UID2",
                1111232,
                "TEST_BLEND_2",
                "TEST_CITY_2",
                "TEST_COUNTRY_2",
                "TEST_VARIETY_1",
                listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_4"),
                "TEST_INTENSIFIER_2"
            ),
            CoffeeModel(
                "TEST_UID3",
                1111232,
                "TEST_BLEND_3",
                "TEST_CITY_1",
                "TEST_COUNTRY_2",
                "TEST_VARIETY_1",
                listOf("TEST_NOTE_1", "TEST_NOTE_4"),
                "TEST_INTENSIFIER_3"
            ),
            CoffeeModel(
                "TEST_UID4",
                1111232,
                "TEST_BLEND_4",
                "TEST_CITY_1",
                "TEST_COUNTRY_3",
                "TEST_VARIETY_1",
                listOf("TEST_NOTE_1", "TEST_NOTE_4"),
                "TEST_INTENSIFIER_4"
            )
        )
    }

    @Test
    fun init_happyTest() {
        `when`(coffeeRepository.getCoffeeList()).thenReturn(flowOf(sampleCoffeeList))
        viewModel = CoffeeListViewModel(coffeeRepository, countryRepository, testDispatcher)
        Assert.assertEquals(sampleCoffeeList, viewModel.coffeeList.value)
        Assert.assertEquals(3, viewModel.coffeeFilters.value?.countries?.size)
        Assert.assertTrue(viewModel.isLoading.value == false)
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.countries?.contains(
                FilterItem("TEST_COUNTRY_1", 1)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.countries?.contains(
                FilterItem("TEST_COUNTRY_2", 2)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.countries?.contains(
                FilterItem("TEST_COUNTRY_3", 1)
            ) ?: false
        )
        Assert.assertEquals(1, viewModel.coffeeFilters.value?.varieties?.size)
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.varieties?.contains(
                FilterItem("TEST_VARIETY_1", 4)
            ) ?: false
        )
        Assert.assertEquals(4, viewModel.coffeeFilters.value?.notes?.size)
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.notes?.contains(
                FilterItem("TEST_NOTE_1", 4)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.notes?.contains(
                FilterItem("TEST_NOTE_2", 2)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.notes?.contains(
                FilterItem("TEST_NOTE_3", 1)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.notes?.contains(
                FilterItem("TEST_NOTE_4", 3)
            ) ?: false
        )
        Assert.assertEquals(4, viewModel.coffeeFilters.value?.intensifiers?.size)
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.intensifiers?.contains(
                FilterItem("TEST_INTENSIFIER_1", 1)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.intensifiers?.contains(
                FilterItem("TEST_INTENSIFIER_2", 1)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.intensifiers?.contains(
                FilterItem("TEST_INTENSIFIER_3", 1)
            ) ?: false
        )
        Assert.assertTrue(
            viewModel.coffeeFilters.value?.intensifiers?.contains(
                FilterItem("TEST_INTENSIFIER_4", 1)
            ) ?: false
        )
    }

    @Test
    fun init_failTest_timeout() {
        val testThrowable = Throwable()
        `when`(coffeeRepository.getCoffeeList()).thenReturn(flow { throw testThrowable })
        Mockito.mockStatic(Log::class.java).use {
            it.`when`<Any> { Log.e(anyString(), anyString(), any()) }.thenReturn(0)
            viewModel = CoffeeListViewModel(coffeeRepository, countryRepository, testDispatcher)
            Assert.assertNull(viewModel.coffeeList.value)
            Assert.assertTrue(viewModel.isError.value?.peek() == testThrowable)
            Assert.assertNull(viewModel.coffeeFilters.value)
        }
    }
}