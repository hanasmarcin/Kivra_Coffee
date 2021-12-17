package com.hanasmarcin.kivracoffee

import android.graphics.drawable.BitmapDrawable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hanasmarcin.kivracoffee.model.repository.CountryRepository
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class CoffeeDetailsViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() // for synchronous execution

    @Mock private val countryRepository = mock(CountryRepository::class.java)
    @Mock private val sampleBitmap = mock(BitmapDrawable::class.java)
    private lateinit var viewModel: CoffeeDetailsViewModel

    init {
        `when`(countryRepository.getFromName(anyString())).thenReturn(sampleBitmap)
    }

    @Before
    fun setup() {
        viewModel = CoffeeDetailsViewModel(countryRepository)
    }

    @Test
    fun getFlagForCountryName_happyTest() {
        val result = viewModel.getFlagForCountryName("test")
        Assert.assertEquals(sampleBitmap, result)
    }
}