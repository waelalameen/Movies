package tech.wa.moviessample.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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
import tech.wa.moviessample.TestConstants.SEARCH_MAIN_TITLE
import tech.wa.moviessample.TestConstants.MOVIE_IN_THE_MIDDLE_POSITION
import tech.wa.moviessample.TestConstants.MOVIE_IN_THE_MIDDLE_TITLE
import tech.wa.moviessample.TestConstants.MOVIE_TITLE
import tech.wa.moviessample.TestMoviesApp_Application
import tech.wa.moviessample.extensions.launchFragmentInHiltContainer

@Config(
    sdk = [30], application = TestMoviesApp_Application::class,
    instrumentedPackages = ["androidx.loader.content"]
)
@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@HiltAndroidTest
class MoviesFragmentTest {

    @get:Rule(order = 0)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val taskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        launchFragmentInHiltContainer<MoviesFragment>(navGraphId = R.navigation.search_navigation)
    }

    // Make sure if fragment is rendered and check fragment title.
    @Test
    fun `A - test movies fragment title - success`() {
        onView(withId(R.id.movies_title_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.movies_title_text_view)).check(matches(withText(SEARCH_MAIN_TITLE)))
    }

    // Scroll recycler view to an item and check if that item is rendered.
    @Test
    fun `B - test scroll to a specified item in the list - success`(): Unit = runBlocking {
        // Check if the search bar is visible.
        onView(withId(R.id.search_input_layout)).check(matches(isDisplayed()))

        // Check that the UI is in idle state.
        onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.GONE)))
         onView(withId(R.id.search_results_recycler_view)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        // Type the movie title to search.
        onView(withId(R.id.search_input)).perform(
            typeText("bond"),
            closeSoftKeyboard()
        )

        // Simulate some delay
        delay(2000)

        // Check that the UI is no longer in loading state.
        onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.search_results_recycler_view)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // Scroll to the item with the given title.
        onView(withId(R.id.search_results_recycler_view)).perform(
            RecyclerViewActions.scrollTo<MoviesAdapter.MoviesViewHolder>(
                hasDescendant(withText(MOVIE_TITLE))
            )
        )
    }

    // Make sure that the item in the middle of list is clicked.
    @Test
    fun `C - test item click on an item in the middle`(): Unit = runBlocking {
        // Check if the search bar is visible.
        onView(withId(R.id.search_input_layout)).check(matches(isDisplayed()))

        // Type the movie title to search.
        onView(withId(R.id.search_input)).perform(
            typeText("bond"),
            closeSoftKeyboard()
        )

        // Simulate some delay
        delay(1000)

        // Click the item with given position.
        onView(withId(R.id.search_results_recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MoviesViewHolder>(
                MOVIE_IN_THE_MIDDLE_POSITION,
                click()
            )
        )

        // Check if it exists on the screen.
        onView(withText(MOVIE_IN_THE_MIDDLE_TITLE)).check(matches(isDisplayed()))
    }
}