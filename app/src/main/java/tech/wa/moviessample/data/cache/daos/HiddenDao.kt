package tech.wa.moviessample.data.cache.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tech.wa.moviessample.data.cache.entities.Hidden

@Dao
interface HiddenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHidden(hidden: Hidden): Long

    @Query("SELECT * FROM hidden")
    suspend fun getAllHidden(): List<Hidden>
}