package com.example.fakestore.di

import android.content.Context
import androidx.room.Room
import com.example.fakestore.data.datastore.UserDataStore
import com.example.fakestore.data.db.FakeStoreDatabase
import com.example.fakestore.data.db.dao.CartDao
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
    fun provideDatabase(@ApplicationContext context: Context): FakeStoreDatabase {
        return Room.databaseBuilder(
            context,
            FakeStoreDatabase::class.java,
            "fake_store_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCartDao(cartDatabase: FakeStoreDatabase): CartDao {
        return cartDatabase.cartDao()
    }

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): UserDataStore {
        return UserDataStore(context)
    }
}