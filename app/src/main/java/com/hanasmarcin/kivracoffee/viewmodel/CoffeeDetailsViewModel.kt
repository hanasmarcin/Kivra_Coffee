package com.hanasmarcin.kivracoffee.viewmodel

import androidx.lifecycle.ViewModel
import com.hanasmarcin.kivracoffee.model.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CoffeeDetailsViewModel @Inject constructor(
    private val countryRepository: CountryRepository
) : ViewModel() {
    fun getFlagForCountryName(name: String) = countryRepository.getFromName(name)
}