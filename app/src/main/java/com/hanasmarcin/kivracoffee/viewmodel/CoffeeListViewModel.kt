package com.hanasmarcin.kivracoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanasmarcin.kivracoffee.di.modules.IoDispatcher
import com.hanasmarcin.kivracoffee.model.CoffeeFilters
import com.hanasmarcin.kivracoffee.model.CoffeeModel
import com.hanasmarcin.kivracoffee.model.filter
import com.hanasmarcin.kivracoffee.model.repository.CoffeeRepository
import com.hanasmarcin.kivracoffee.model.repository.CountryRepository
import com.hanasmarcin.kivracoffee.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CoffeeListViewModel @Inject constructor(
    private val coffeeRepository: CoffeeRepository,
    private val countryRepository: CountryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _isError = MutableLiveData<Event<Throwable>>()
    val isError: LiveData<Event<Throwable>> get() = _isError

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _coffeeList = MutableLiveData<List<CoffeeModel>>()
    val coffeeList: LiveData<List<CoffeeModel>> get() = _coffeeList

    private var fullCoffeeList: List<CoffeeModel> = listOf()

    private val _coffeeFilters = MutableLiveData<CoffeeFilters>()
    val coffeeFilters: LiveData<CoffeeFilters> get() = _coffeeFilters

    init {
        viewModelScope.launch(ioDispatcher) {
            coffeeRepository.getCoffeeList()
                .catch { handleException(it) }
                .onEach {
                    fullCoffeeList = it
                    _coffeeList.postValue(it)
                    _isLoading.postValue(false)
                    _coffeeFilters.postValue(CoffeeFilters.fromCoffeeList(it))
                }
                .launchIn(this)
        }
    }

    fun getFlagForCountryName(name: String) = countryRepository.getFromName(name)

    fun filterCoffees(filters: CoffeeFilters) {
        _coffeeList.value = fullCoffeeList.filter(filters)
    }

    private fun handleException(error: Throwable) {
        Timber.e(error)
        _isLoading.postValue(false)
        _isError.postValue(Event(error))
    }
}
