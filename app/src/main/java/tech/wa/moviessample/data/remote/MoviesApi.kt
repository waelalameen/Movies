package tech.wa.moviessample.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import tech.wa.moviessample.data.Constants.BASE_URL
import tech.wa.moviessample.data.remote.models.DetailsDto
import tech.wa.moviessample.data.remote.models.ResultsResponse

interface MoviesApi {

    @GET(BASE_URL)
    suspend fun search(
        @Query("s") name: String = "",
        @Query("page") page: Int = 1
    ): Response<ResultsResponse>

    @GET(BASE_URL)
    suspend fun details(@Query("i") id: String): Response<DetailsDto>
}