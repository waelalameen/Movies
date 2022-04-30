package tech.wa.moviessample.ui.favorites

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
import tech.wa.moviessample.TestConstants.FAVORITES_MAIN_TITLE
import tech.wa.moviessample.TestMoviesApp_Application
import tech.wa.moviessample.extensions.launchFragmentInHiltContainer

@Config(
    sdk = [30], application = TestMoviesApp_Application::class,
    instrumentedPackages = ["androidx.loader.content"]
)
@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@HiltAndroidTest
class FavoritesFragmentTest {

    @get:Rule(order = 0)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val taskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        launchFragmentInHiltContainer<FavoritesFragment>(navGraphId = R.navigation.favorites_navigation)
    }

    // Make sure if fragment is rendered and check fragment title.
    @Test
    fun `A - test favorite movies fragment title - success`() {
        onView(withId(R.id.favorites_title_text_view)).check(matches(isDisplayed()))
        onView(withId(R.id.favorites_title_text_view)).check(matches(withText(FAVORITES_MAIN_TITLE)))
    }

    // Check that recyclerview comes into view.
    @Test
    fun `B - test if recycler view is visible after loading finishes - success if data exists`(): Unit = runBlocking {
         onView(withId(R.id.favorites_recycler_view)).check(matches(withEffectiveVisibility(Visibility.GONE)))
         onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

         delay(3000)

         onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.GONE)))
         onView(withId(R.id.favorites_recycler_view)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    // Check that recyclerview comes into view.
    @Test
    fun `C - test when no data are available - success if no data`(): Unit = runBlocking {
        onView(withId(R.id.favorites_recycler_view)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        delay(5000)

        onView(withId(R.id.layout_loading)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.favorites_recycler_view)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.layout_empty)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}