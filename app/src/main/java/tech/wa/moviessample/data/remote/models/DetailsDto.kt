package tech.wa.moviessample.data.remote.models

import com.google.gson.annotations.SerializedName
import tech.wa.moviessample.domain.Details

data class DetailsDto(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("Rated")
    val rateType: String,
    @SerializedName("Released")
    val releaseDate: String,
    @SerializedName("Runtime")
    val duration: String,
    @SerializedName("Genre")
    val genre: String,
    @SerializedName("Director")
    val directorName: String,
    @SerializedName("Writer")
    val writers: String,
    @SerializedName("Actors")
    val actors: String,
    @SerializedName("Plot")
    val plot: String,
    @SerializedName("Language")
    val language: String,
    @SerializedName("Country")
    val country: String,
    @SerializedName("Awards")
    val awards: String,
    @SerializedName("Poster")
    val poster: String,
    @SerializedName("imdbRating")
    val rating: String,
    @SerializedName("imdbVotes")
    val votes: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("BoxOffice")
    val boxOfficeRevenue: String,
    @SerializedName("Response")
    val responseStatus: String,
    @SerializedName("Error")
    val errorMessage: String
) {
    fun toDetails(): Details {
        return Details(
            title = title,
            year = year,
            rateType = rateType,
            releaseDate = releaseDate,
            duration = duration,
            genre = genre,
            directorName = directorName,
            writers = writers,
            actors = actors,
            plot = plot,
            language = language,
            country = country,
            awards = awards,
            poster = poster,
            rating = rating,
            votes = votes,
            type = type,
            boxOfficeRevenue = boxOfficeRevenue,
        )
    }
}