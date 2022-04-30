package tech.wa.moviessample.domain

import tech.wa.moviessample.data.cache.entities.FavoritesEntity

data class Favorites(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val year: String = "",
    val poster: String = ""
) {
    fun toFavoriteDto(): FavoritesEntity {
        return FavoritesEntity(
            id = id,
            title = title,
            type = type,
            year = year,
            poster = poster
        )
    }
}