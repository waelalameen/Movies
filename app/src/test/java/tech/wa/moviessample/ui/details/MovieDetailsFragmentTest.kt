package tech.wa.moviessample.ui.details

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.wa.moviessample.R
import tech.wa.moviessample.TestConstants.DETAILS_MAIN_TITLE
import tech.wa.moviessample.TestConstants.ID
import tech.wa.moviessample.TestConstants.MOVIE_DETAILS_DESCRIPTION
import tech.wa.moviessample.TestConstants.MOVIE_DETAILS_TITLE
import tech.wa.moviessample.TestMoviesApp_Application
import tech.wa.moviessample.extensions.launchFragmentInHiltContainer

@Config(
    sdk = [30], application = TestMoviesApp_Application::class,
    instrumentedPackages = ["androidx.loader.content"]
)
@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@HiltAndroidTest
class MovieDetailsFragmentTest {

    @get:Rule(order = 0)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val taskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val bundle = Bundle()
        bundle.putString(ID, "tt0106364")

        launchFragmentInHiltContainer<MovieDetailsFragment>(
            navGraphId = R.navigation.search_navigation,
            fragmentArgs = bundle
        )
    }

    // Make sure if fragment is rendered and check fragment title.
    @Test
    fun `A - test movie details fragment title - success`() {
        onView(withId(R.id.details_title_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.details_title_text_view)).check(matches(withText(DETAILS_MAIN_TITLE)))
    }

    // Make sure that data comes right after loading state finishes.
    @Test
    fun `B - test if data comes right after loading state finishes - success`(): Unit = runBlocking {
        // Simulate some delay.
        delay(3000)

        // Make sure loading has gone.
        onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        // Make sure the details layout is now visible.
        onView(withId(R.id.layout_details)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    // Checking that the required data was delivered correctly.
    @Test
    fun `C - test if the required data was delivered correctly`(): Unit = runBlocking {
        // Simulate some delay.
        delay(3000)

        // Assert if poster is visible.
        onView(withId(R.id.poster_image_view)).check(matches(isDisplayed()))

        // Assert if title text is visible and showing correct title.
        onView(withId(R.id.movie_title_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.movie_title_text_view)).check(matches(withText(MOVIE_DETAILS_TITLE)))

        // Assert if description text is visible and showing correct description.
        onView(withId(R.id.movie_desc_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.movie_desc_text_view)).check(matches(withText(MOVIE_DETAILS_DESCRIPTION)))
    }
}