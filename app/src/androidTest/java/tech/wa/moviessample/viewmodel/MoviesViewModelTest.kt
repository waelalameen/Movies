package tech.wa.moviessample.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.data.repository.details.DetailsRepositoryImpl
import tech.wa.moviessample.data.repository.options.OptionsRepositoryImpl
import tech.wa.moviessample.presentation.MoviesViewModel
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
class MoviesViewModelTest {

    @get:Rule
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var api: MoviesApi

    @Inject
    lateinit var favoritesDao: FavoritesDao

    @Inject
    lateinit var hiddenDao: HiddenDao

    private lateinit var viewModel: MoviesViewModel

    @Before
    fun setup() {
        hiltTestRule.inject()

        val detailsRepository = DetailsRepositoryImpl(api)
        val optionsRepository = OptionsRepositoryImpl(favoritesDao, hiddenDao)

        viewModel = MoviesViewModel(api, detailsRepository, optionsRepository)
    }

    @Test
    fun fetch_movie_or_show_details_success(): Unit = runBlocking {
        // Get details of the movie with the give id.
        viewModel.getDetails("tt0106364")

        // Wait to obtain the result from StateFlow with some delay.
        viewModel.detailsState.test(timeoutMs = 2000) {
            // Assert that the first emission is loading,
            assertEquals(UiState.State.LOADING, awaitItem().state)

            // Assert that the second emission is actual data.
            val item = awaitItem()
            assertEquals(UiState.State.SUCCESS, item.state)

            // Make sure the emitted data is not null.
            assertTrue { item.data != null }

            // Assert that there's no more loading after fetching the data.
            assertNotEquals(UiState.State.LOADING, item.state)

            // Cancel other flow events.
            cancelAndIgnoreRemainingEvents()
        }
    }
}