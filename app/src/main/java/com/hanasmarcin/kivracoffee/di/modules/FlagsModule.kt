package com.hanasmarcin.kivracoffee.di.modules

import android.content.Context
import com.hanasmarcin.kivracoffee.utils.Flags
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FlagsModule {

    @Provides
    fun provideFlags(@ApplicationContext context: Context): Flags {
        return Flags.Builder(context).setTileHeight(80).setTileWidth(128).build()
    }
}