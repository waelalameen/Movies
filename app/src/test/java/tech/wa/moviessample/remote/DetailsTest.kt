package tech.wa.moviessample.remote

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.wa.moviessample.data.Constants.BASE_URL
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.data.repository.details.DetailsRepository
import tech.wa.moviessample.data.repository.details.DetailsRepositoryImpl
import tech.wa.moviessample.domain.Details
import tech.wa.moviessample.remote.MockResponses.DETAILS_ERROR_RESPONSE
import tech.wa.moviessample.remote.MockResponses.DETAILS_RESPONSE
import java.net.HttpURLConnection
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DetailsTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var repository: DetailsRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url(BASE_URL)

        val mockMoviesApi = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesApi::class.java)

        repository = DetailsRepositoryImpl(mockMoviesApi)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `fetch movie or show details - success`(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(DETAILS_RESPONSE))

        val flows = repository.getDetails(id = "tt0106364").toList()

        assertTrue { flows[0] is UiState.Loading }

        val data = flows[1].data

        assertTrue { data is Details }

        assertFalse { flows[1] is UiState.Loading }
    }

    @Test
    fun `fetch movie or show details that does not exist - fail`(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(DETAILS_ERROR_RESPONSE))

        // Send invalid id in the request
        val flows = repository.getDetails(id = "tt010636458").toList()

        assertTrue { flows[0] is UiState.Loading }

        val data = flows[1]

        assertTrue { data is UiState.Error }

        val errorMessage = data.errorMessage
        assertTrue { errorMessage == "Incorrect IMDb ID." }

        assertFalse { flows[1] is UiState.Loading }
    }
}