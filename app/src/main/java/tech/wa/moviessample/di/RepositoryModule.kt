package tech.wa.moviessample.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.data.repository.details.DetailsRepository
import tech.wa.moviessample.data.repository.details.DetailsRepositoryImpl
import tech.wa.moviessample.data.repository.options.OptionsRepository
import tech.wa.moviessample.data.repository.options.OptionsRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun providesDetailsRepository(api: MoviesApi): DetailsRepository = DetailsRepositoryImpl(api)

    @Provides
    fun providesOptionsRepository(
        favoritesDao: FavoritesDao,
        hiddenDao: HiddenDao
    ): OptionsRepository = OptionsRepositoryImpl(favoritesDao, hiddenDao)
}