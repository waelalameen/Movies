package tech.wa.moviessample.data.remote.models

import com.google.gson.annotations.SerializedName
import tech.wa.moviessample.domain.Search

data class SearchDto(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val id: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String
) {
    fun toSearch(): Search {
        return Search(
            title = title,
            year = year,
            id = id,
            type = type,
            poster = poster
        )
    }
}
