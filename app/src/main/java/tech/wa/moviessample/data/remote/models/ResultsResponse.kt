package tech.wa.moviessample.data.remote.models

import com.google.gson.annotations.SerializedName

data class ResultsResponse(
    @SerializedName("Search")
    val searchDtoResults: List<SearchDto>,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Response")
    val responseStatus: String,
    @SerializedName("Error")
    val errorMessage: String
)
