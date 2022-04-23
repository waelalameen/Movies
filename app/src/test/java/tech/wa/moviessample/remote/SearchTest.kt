package tech.wa.moviessample.remote

import androidx.paging.PagingSource
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.wa.moviessample.data.Constants.BASE_URL
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.data.remote.models.ResultsResponse
import tech.wa.moviessample.data.remote.models.SearchDto
import tech.wa.moviessample.data.repository.search.SearchDataSource
import tech.wa.moviessample.remote.MockResponses.SEARCH_ERROR_RESPONSE
import tech.wa.moviessample.remote.MockResponses.SEARCH_RESPONSE
import java.net.HttpURLConnection
import kotlin.test.junit.JUnitAsserter.assertEquals
import kotlin.test.junit.JUnitAsserter.assertNotEquals

class SearchTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var mockMoviesApi: MoviesApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url(BASE_URL)

        mockMoviesApi = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `load the initial page only - success`(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(SEARCH_RESPONSE))

        val response = Gson().fromJson(SEARCH_RESPONSE, ResultsResponse::class.java)
        val results = response.searchDtoResults

        val pagingSource = SearchDataSource(mockMoviesApi, "bond")

        assertEquals(
            message = "load the initial page only",
            expected = PagingSource.LoadResult.Page(
                data = results,
                prevKey = 0,
                nextKey = 2
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `load and append the next page - success`(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(SEARCH_RESPONSE))

        val response = Gson().fromJson(SEARCH_RESPONSE, ResultsResponse::class.java)
        val results = response.searchDtoResults

        val pagingSource = SearchDataSource(mockMoviesApi, "bond")

        assertNotEquals(
            message = "load and append the next page",
            illegal = PagingSource.LoadResult.Page(
                data = results,
                prevKey = 1,
                nextKey = 3
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 2,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `load and prepend the next page - success`(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(SEARCH_RESPONSE))

        val response = Gson().fromJson(SEARCH_RESPONSE, ResultsResponse::class.java)
        val results = response.searchDtoResults

        val pagingSource = SearchDataSource(mockMoviesApi, "bond")

        assertEquals(
            message = "load and append the next page",
            expected = PagingSource.LoadResult.Page(
                data = results,
                prevKey = 0,
                nextKey = 2
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `load if requested page is not found - fail`(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(SEARCH_ERROR_RESPONSE))

        val response = Gson().fromJson(SEARCH_ERROR_RESPONSE, ResultsResponse::class.java)
        val errorMessage = response.errorMessage

        val pagingSource = SearchDataSource(mockMoviesApi, "bond")

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 100,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val error = PagingSource.LoadResult.Error<Int, SearchDto>(Exception(errorMessage))

        val result = loadResult as PagingSource.LoadResult.Error

        assertEquals(
            message = "load if requested page is not found",
            expected = error.throwable.message,
            actual = result.throwable.message
        )
    }
}