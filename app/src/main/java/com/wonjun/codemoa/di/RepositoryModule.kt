package com.wonjun.codemoa.di

import android.content.Context
import com.wonjun.codemoa.data.database.CardDao
import com.wonjun.codemoa.data.repository.CardRepository
import com.wonjun.codemoa.data.repository.CardRepositoryImpl
import com.wonjun.codemoa.data.repository.SettingsRepository
import com.wonjun.codemoa.data.repository.SettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCardRepository(
        cardDao: CardDao
    ): CardRepository {
        return CardRepositoryImpl(cardDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}