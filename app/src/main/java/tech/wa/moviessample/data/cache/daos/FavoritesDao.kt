package tech.wa.moviessample.data.cache.daos

import androidx.room.*
import tech.wa.moviessample.data.cache.entities.FavoritesEntity

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoritesEntity): Long

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoritesEntity>

    @Query("SELECT * FROM favorites WHERE id=:id")
    suspend fun getFavorite(id: String): FavoritesEntity?

    @Query("DELETE FROM favorites WHERE id=:id")
    suspend fun removeFromFavorites(id: String)
}