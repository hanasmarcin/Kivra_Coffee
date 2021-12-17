package com.hanasmarcin.kivracoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanasmarcin.kivracoffee.di.modules.IoDispatcher
import com.hanasmarcin.kivracoffee.model.CoffeeFilters
import com.hanasmarcin.kivracoffee.model.FilterItem
import com.hanasmarcin.kivracoffee.model.FilterType
import com.hanasmarcin.kivracoffee.model.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoffeeFiltersViewModel @Inject constructor(
    @IoDispatcher var mainDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _coffeeFilters = MutableLiveData<CoffeeFilters>()
    val coffeeFilters: LiveData<CoffeeFilters>
        get() = _coffeeFilters

    fun initFilters(coffeeFiltersData: CoffeeFilters) = viewModelScope.launch(mainDispatcher) {
        val old = _coffeeFilters.value
        _coffeeFilters.postValue(
            if (old == null) coffeeFiltersData else CoffeeFilters.merge(old, coffeeFiltersData)
        )
    }

    fun selectSort(sortType: SortType) {
        _coffeeFilters.value = _coffeeFilters.value?.copy(sortType = sortType)
    }

    fun changeFilterItemState(item: FilterItem, filterType: FilterType, isSelected: Boolean) {
        _coffeeFilters.value?.changeFilterItemState(item, filterType, isSelected)
        _coffeeFilters.value = _coffeeFilters.value?.copy()
    }

    fun clearAllFilters() {
        _coffeeFilters.value?.clearAllFilters()
        _coffeeFilters.value = _coffeeFilters.value?.copy()
    }
}