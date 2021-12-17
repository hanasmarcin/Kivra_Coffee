package com.hanasmarcin.kivracoffee.model.service

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Service allowing calls to API
 */
interface CoffeeApiService {

    /**
     * Get full list of coffees
     * @param size number of items in the response
     * @return list of coffees
     */
    @GET("/api/coffee/random_coffee")
    suspend fun getCoffeeList(
        @Query("size") size: Int = 30
    ): List<CoffeeApiModel>
}