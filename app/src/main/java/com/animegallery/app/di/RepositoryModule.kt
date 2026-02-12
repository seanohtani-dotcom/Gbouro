package com.animegallery.app.di

import com.animegallery.app.data.repository.AuthRepositoryImpl
import com.animegallery.app.data.repository.DownloadRepositoryImpl
import com.animegallery.app.data.repository.FavoritesRepositoryImpl
import com.animegallery.app.data.repository.ImageRepositoryImpl
import com.animegallery.app.data.repository.TagRepositoryImpl
import com.animegallery.app.domain.repository.AuthRepository
import com.animegallery.app.domain.repository.DownloadRepository
import com.animegallery.app.domain.repository.FavoritesRepository
import com.animegallery.app.domain.repository.ImageRepository
import com.animegallery.app.domain.repository.TagRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        favoritesRepositoryImpl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        downloadRepositoryImpl: DownloadRepositoryImpl
    ): DownloadRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository
}
