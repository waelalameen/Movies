package tech.wa.moviessample.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.cache.entities.FavoritesEntity
import tech.wa.moviessample.data.cache.entities.HiddenEntity

@Database(version = 1, entities = [FavoritesEntity::class, HiddenEntity::class], exportSchema = false)
abstract class MoviesDatabase: RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    abstract fun hiddenDao(): HiddenDao
}