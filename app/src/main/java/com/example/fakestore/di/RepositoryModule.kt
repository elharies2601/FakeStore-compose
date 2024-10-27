package com.example.fakestore.di

import com.example.fakestore.data.repository.CartRepositoryImpl
import com.example.fakestore.data.repository.ProductRepositoryImpl
import com.example.fakestore.data.repository.UserRepositoryImpl
import com.example.fakestore.domain.CartRepository
import com.example.fakestore.domain.ProductRepository
import com.example.fakestore.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [DatabaseModule::class, NetworkModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCartRepository(repository: CartRepositoryImpl): CartRepository

    @Binds
    abstract fun bindProductRepository(repository: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
}