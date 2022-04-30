package tech.wa.moviessample.data.cache.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.wa.moviessample.domain.Favorites

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: String,
    val year: String,
    val poster: String
) {
    fun toFavorite(): Favorites {
        return Favorites(
            id = id,
            title = title,
            type = type,
            year = year,
            poster = poster
        )
    }
}
