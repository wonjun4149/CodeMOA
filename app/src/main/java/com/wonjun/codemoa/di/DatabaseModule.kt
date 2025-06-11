package com.wonjun.codemoa.di

import android.content.Context
import androidx.room.Room
import com.wonjun.codemoa.data.database.CardDao
import com.wonjun.codemoa.data.database.CardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCardDatabase(
        @ApplicationContext context: Context
    ): CardDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CardDatabase::class.java,
            "card_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCardDao(database: CardDatabase): CardDao = database.cardDao()
}