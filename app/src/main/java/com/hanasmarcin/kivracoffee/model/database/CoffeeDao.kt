package com.hanasmarcin.kivracoffee.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.hanasmarcin.kivracoffee.model.CoffeeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {

    @Query("SELECT * FROM coffeemodel")
    fun getAll(): Flow<List<CoffeeModel>>

    @Insert
    suspend fun insert(coffee: CoffeeModel)

    @Query("DELETE FROM coffeemodel")
    suspend fun delete()

    @Insert
    suspend fun insertAll(coffees: List<CoffeeModel>)

    @Transaction
    suspend fun replaceAll(coffees: List<CoffeeModel>) {
        delete()
        insertAll(coffees)
    }
}