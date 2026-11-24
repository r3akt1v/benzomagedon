package com.benzomageddon.di

import android.content.Context
import com.benzomageddon.data.AppDatabase
import com.benzomageddon.data.dao.CategoryDao
import com.benzomageddon.data.dao.TransactionDao
import com.benzomageddon.data.network.ExchangeRateApi
import com.benzomageddon.data.repository.CategoryRepository
import com.benzomageddon.data.repository.RatesRepository
import com.benzomageddon.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideExchangeRateApi(retrofit: Retrofit): ExchangeRateApi =
        retrofit.create(ExchangeRateApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao) = TransactionRepository(dao)

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao) = CategoryRepository(dao)

    @Provides
    @Singleton
    fun provideRatesRepository(api: ExchangeRateApi) = RatesRepository(api)
}
