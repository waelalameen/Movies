package tech.wa.moviessample.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.cache.entities.FavoritesDto
import tech.wa.moviessample.data.cache.entities.Hidden

@Database(version = 1, entities = [FavoritesDto::class, Hidden::class], exportSchema = false)
abstract class MoviesDatabase: RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    abstract fun hiddenDao(): HiddenDao
}