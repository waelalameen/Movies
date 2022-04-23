package tech.wa.moviessample.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tech.wa.moviessample.data.Constants
import tech.wa.moviessample.data.cache.MoviesDatabase
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideMoviesDatabase(@ApplicationContext context: Context): MoviesDatabase {
        return Room.databaseBuilder(context, MoviesDatabase::class.java, Constants.MOVIES_DB).build()
    }

    @Singleton
    @Provides
    fun provideFavoritesDao(database: MoviesDatabase): FavoritesDao {
        return database.favoritesDao()
    }

    @Singleton
    @Provides
    fun provideHiddenDao(database: MoviesDatabase): HiddenDao {
        return database.hiddenDao()
    }
}