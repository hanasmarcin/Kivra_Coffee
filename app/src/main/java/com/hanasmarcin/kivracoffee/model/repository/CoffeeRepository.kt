package com.hanasmarcin.kivracoffee.model.repository

import com.hanasmarcin.kivracoffee.model.CoffeeModel
import com.hanasmarcin.kivracoffee.model.database.CoffeeDao
import com.hanasmarcin.kivracoffee.model.service.CoffeeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

const val CACHE_TIME = 300000L

/**
 * Repository class for getting coffee from DB and API
 */
@Singleton
@ExperimentalCoroutinesApi
class CoffeeRepository @Inject constructor(
    private val coffeeApiService: CoffeeApiService,
    private val coffeeDao: CoffeeDao
) {
    fun getCoffeeList(): Flow<List<CoffeeModel>> =
        coffeeDao.getAll().flatMapLatest { coffees ->
            val currentTimestamp = System.currentTimeMillis()
            // if data is expired or db is not populated with data, fetch from API and replace in db
            if (coffees.any { it.fetchTimestamp + CACHE_TIME < currentTimestamp } || coffees.isEmpty()) {
                val newCoffees = coffeeApiService.getCoffeeList()
                    .map { CoffeeModel.fromApiModel(it, currentTimestamp) }
                coffeeDao.replaceAll(newCoffees)
                flowOf(newCoffees)
            } else flowOf(coffees)
        }
}