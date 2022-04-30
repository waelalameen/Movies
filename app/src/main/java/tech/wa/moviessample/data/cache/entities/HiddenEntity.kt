package tech.wa.moviessample.data.cache.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.wa.moviessample.domain.Search

@Entity(tableName = "hidden")
data class HiddenEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: String,
    val year: String,
    val poster: String
) {
    fun toSearchResult(): Search {
        return Search(
            id = id,
            title = title,
            type = type,
            year = year,
            poster = poster
        )
    }
}
