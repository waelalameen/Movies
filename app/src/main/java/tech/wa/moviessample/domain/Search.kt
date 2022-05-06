package tech.wa.moviessample.domain

data class Search(
    val title: String = "",
    val year: String = "",
    val id: String = "",
    val type: String = "",
    val poster: String = "",
    var isFavorite: Boolean = false,
    var isDeleted: Boolean = false
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

    fun toHidden(): Hidden {
        return Hidden(
            id = id,
            title = title,
            type = type,
            year = year,
            poster = poster
        )
    }
}
