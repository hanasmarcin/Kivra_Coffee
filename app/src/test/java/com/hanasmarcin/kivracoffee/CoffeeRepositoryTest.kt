package com.hanasmarcin.kivracoffee

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hanasmarcin.kivracoffee.model.CoffeeModel
import com.hanasmarcin.kivracoffee.model.database.CoffeeDao
import com.hanasmarcin.kivracoffee.model.repository.CoffeeRepository
import com.hanasmarcin.kivracoffee.model.service.CoffeeApiModel
import com.hanasmarcin.kivracoffee.model.service.CoffeeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CoffeeRepositoryTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() // for synchronous execution

    @Mock
    private val coffeeApiService: CoffeeApiService = mock(CoffeeApiService::class.java)

    @Mock
    private val coffeeDao: CoffeeDao = mock(CoffeeDao::class.java)

    private lateinit var coffeeRepository: CoffeeRepository
    private lateinit var coffeeModel1: CoffeeModel
    private lateinit var coffeeModel2: CoffeeModel
    private lateinit var coffeeApiModel1: CoffeeApiModel
    private lateinit var coffeeApiModel2: CoffeeApiModel

    @Before
    fun setup() {
        coffeeModel1 = CoffeeModel(
            "TEST_UID_1",
            System.currentTimeMillis(),
            "TEST_BLEND_1",
            "TEST_CITY_1, TEST_COUNTRY_1",
            "TEST_COUNTRY_1",
            "TEST_VARIETY_1",
            listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_3"),
            "TEST_INTENSIFIER_1"
        )
        coffeeModel2 = CoffeeModel(
            "TEST_UID_2",
            System.currentTimeMillis(),
            "TEST_BLEND_2",
            "TEST_CITY_2, TEST_COUNTRY_2",
            "TEST_COUNTRY_2",
            "TEST_VARIETY_2",
            listOf("TEST_NOTE_1", "TEST_NOTE_4", "TEST_NOTE_5"),
            "TEST_INTENSIFIER_2"
        )

        coffeeApiModel1 = CoffeeApiModel(
            1,
            "TEST_UID1",
            "TEST_BLEND_1",
            "TEST_CITY_1, TEST_COUNTRY_1",
            "TEST_VARIETY_1",
            "TEST_NOTE_1, TEST_NOTE_2, TEST_NOTE_3",
            "TEST_INTENSIFIER_1"
        )
        coffeeApiModel2 = CoffeeApiModel(
            1,
            "TEST_UID3",
            "TEST_BLEND_3",
            "TEST_CITY_3, TEST_COUNTRY_3",
            "TEST_VARIETY_3",
            "TEST_NOTE_2, TEST_NOTE_4, TEST_NOTE_5",
            "TEST_INTENSIFIER_3"
        )
        coffeeRepository = CoffeeRepository(coffeeApiService, coffeeDao)
    }

    @Test
    fun getCoffeeList_happyTest_dataFromApiOnly() {
        runBlocking {
            `when`(coffeeApiService.getCoffeeList(anyInt())).thenReturn(listOf(coffeeApiModel1, coffeeApiModel2))
            `when`(coffeeDao.getAll()).thenReturn(flow { emit(listOf<CoffeeModel>()) })
            val result = coffeeRepository.getCoffeeList().first()
            Assert.assertEquals(2, result.size)
            Assert.assertTrue(result.any {
                "TEST_BLEND_1" == it.blendName &&
                    "TEST_CITY_1, TEST_COUNTRY_1" == it.origin &&
                    "TEST_COUNTRY_1" == it.country &&
                    "TEST_VARIETY_1" == it.variety &&
                    listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_3") == it.notes &&
                    "TEST_INTENSIFIER_1" == it.intensifier
            })
            Assert.assertTrue(result.any {
                "TEST_BLEND_3" == it.blendName &&
                    "TEST_CITY_3, TEST_COUNTRY_3" == it.origin &&
                    "TEST_COUNTRY_3" == it.country &&
                    "TEST_VARIETY_3" == it.variety &&
                    listOf("TEST_NOTE_2", "TEST_NOTE_4", "TEST_NOTE_5") == it.notes &&
                    "TEST_INTENSIFIER_3" == it.intensifier
            })
        }
    }

    @Test
    fun getCoffeeList_happyTest_dataFromBDOnly() {
        runBlocking {
            `when`(coffeeApiService.getCoffeeList(anyInt())).thenReturn(listOf())
            `when`(coffeeDao.getAll()).thenReturn(flow { emit(listOf(coffeeModel1, coffeeModel2)) })
            val result = coffeeRepository.getCoffeeList().first()
            Assert.assertEquals(2, result.size)
            Assert.assertTrue(result.any {
                "TEST_BLEND_1" == it.blendName &&
                    "TEST_CITY_1, TEST_COUNTRY_1" == it.origin &&
                    "TEST_COUNTRY_1" == it.country &&
                    "TEST_VARIETY_1" == it.variety &&
                    listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_3") == it.notes &&
                    "TEST_INTENSIFIER_1" == it.intensifier
            })
            Assert.assertTrue(result.any {
                "TEST_BLEND_2" == it.blendName &&
                    "TEST_CITY_2, TEST_COUNTRY_2" == it.origin &&
                    "TEST_COUNTRY_2" == it.country &&
                    "TEST_VARIETY_2" == it.variety &&
                    listOf("TEST_NOTE_1", "TEST_NOTE_4", "TEST_NOTE_5") == it.notes &&
                    "TEST_INTENSIFIER_2" == it.intensifier
            })
        }
    }

    @Test
    fun getCoffeeList_happyTest_dataFromBBAndApi() {
        runBlocking {
            `when`(coffeeApiService.getCoffeeList(anyInt())).thenReturn(listOf(coffeeApiModel1, coffeeApiModel2))
            `when`(coffeeDao.getAll()).thenReturn(flow { emit(listOf(coffeeModel1, coffeeModel2)) })
            val result = coffeeRepository.getCoffeeList().first()
            Assert.assertEquals(2, result.size)
            Assert.assertTrue(result.any {
                "TEST_BLEND_1" == it.blendName &&
                    "TEST_CITY_1, TEST_COUNTRY_1" == it.origin &&
                    "TEST_COUNTRY_1" == it.country &&
                    "TEST_VARIETY_1" == it.variety &&
                    listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_3") == it.notes &&
                    "TEST_INTENSIFIER_1" == it.intensifier
            })
            Assert.assertTrue(result.any {
                "TEST_BLEND_2" == it.blendName &&
                    "TEST_CITY_2, TEST_COUNTRY_2" == it.origin &&
                    "TEST_COUNTRY_2" == it.country &&
                    "TEST_VARIETY_2" == it.variety &&
                    listOf("TEST_NOTE_1", "TEST_NOTE_4", "TEST_NOTE_5") == it.notes &&
                    "TEST_INTENSIFIER_2" == it.intensifier
            })
        }
    }

    @Test
    fun getCoffeeList_happyTest_expiredDBData() {
        runBlocking {
            `when`(coffeeApiService.getCoffeeList(anyInt())).thenReturn(listOf(coffeeApiModel1, coffeeApiModel2))
            `when`(coffeeDao.getAll()).thenReturn(flow { emit(listOf(coffeeModel1.copy(fetchTimestamp = -1), coffeeModel2.copy(fetchTimestamp = -1))) })
            val result = coffeeRepository.getCoffeeList().first()
            Assert.assertEquals(2, result.size)
            Assert.assertTrue(result.any {
                "TEST_BLEND_1" == it.blendName &&
                    "TEST_CITY_1, TEST_COUNTRY_1" == it.origin &&
                    "TEST_COUNTRY_1" == it.country &&
                    "TEST_VARIETY_1" == it.variety &&
                    listOf("TEST_NOTE_1", "TEST_NOTE_2", "TEST_NOTE_3") == it.notes &&
                    "TEST_INTENSIFIER_1" == it.intensifier
            })
            Assert.assertTrue(result.any {
                "TEST_BLEND_3" == it.blendName &&
                    "TEST_CITY_3, TEST_COUNTRY_3" == it.origin &&
                    "TEST_COUNTRY_3" == it.country &&
                    "TEST_VARIETY_3" == it.variety &&
                    listOf("TEST_NOTE_2", "TEST_NOTE_4", "TEST_NOTE_5") == it.notes &&
                    "TEST_INTENSIFIER_3" == it.intensifier
            })
        }
    }
}