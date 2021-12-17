package com.hanasmarcin.kivracoffee.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hanasmarcin.kivracoffee.BuildConfig.DATABASE_VERSION
import com.hanasmarcin.kivracoffee.model.CoffeeModel


@Database(entities = [CoffeeModel::class], version = DATABASE_VERSION)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
}

class Converters {
    @TypeConverter
    fun listToString(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun stringToList(value: String): List<String> {
        val listType = object : TypeToken<List<String>?>() {}.type
        return Gson().fromJson(value, listType)
    }
}
