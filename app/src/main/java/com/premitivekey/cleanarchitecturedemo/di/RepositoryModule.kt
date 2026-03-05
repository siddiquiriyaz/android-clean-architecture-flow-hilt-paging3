package com.premitivekey.cleanarchitecturedemo.di

import com.premitivekey.cleanarchitecturedemo.data.repository.UserRepositoryImpl
import com.premitivekey.cleanarchitecturedemo.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

}