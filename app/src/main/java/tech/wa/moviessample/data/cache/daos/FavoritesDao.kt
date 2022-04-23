package tech.wa.moviessample.data.cache.daos

import androidx.room.*
import tech.wa.moviessample.data.cache.entities.FavoritesDto

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoritesDto): Long

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoritesDto>

    @Query("SELECT * FROM favorites WHERE id=:id")
    suspend fun getFavorite(id: String): FavoritesDto?

    @Query("DELETE FROM favorites WHERE id=:id")
    suspend fun removeFromFavorites(id: String)
}