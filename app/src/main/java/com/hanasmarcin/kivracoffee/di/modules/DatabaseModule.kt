package com.hanasmarcin.kivracoffee.di.modules

import android.content.Context
import androidx.room.Room
import com.hanasmarcin.kivracoffee.model.database.AppDatabase
import com.hanasmarcin.kivracoffee.model.database.CoffeeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideCoffeeDao(appDatabase: AppDatabase): CoffeeDao {
        return appDatabase.coffeeDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "coffees"
        ).fallbackToDestructiveMigration().build()
    }
}