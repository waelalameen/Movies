package tech.wa.moviessample.data.cache

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.di.CacheModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CacheModule::class]
)
object FakeCacheModule {

    @Singleton
    @Provides
    fun provideMoviesDatabase(@ApplicationContext context: Context): MoviesDatabase {
        return Room.inMemoryDatabaseBuilder(context, MoviesDatabase::class.java).build()
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