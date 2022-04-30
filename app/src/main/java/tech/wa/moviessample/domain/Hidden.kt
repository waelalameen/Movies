package tech.wa.moviessample.domain

import tech.wa.moviessample.data.cache.entities.HiddenEntity

data class Hidden(
    val id: String,
    val title: String,
    val type: String,
    val year: String,
    val poster: String
) {
    fun toHiddenEntity(): HiddenEntity {
        return HiddenEntity(
            id = id,
            title = title,
            type = type,
            year = year,
            poster = poster
        )
    }
}