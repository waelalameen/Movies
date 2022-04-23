package tech.wa.moviessample.data.cache

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.di.CacheModule
import javax.inject.Inject
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@UninstallModules(CacheModule::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@HiltAndroidTest
class MoviesDatabaseTest {

    @get:Rule
    var hiltTestRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MoviesDatabase

    private lateinit var dao: FavoritesDao

    @Before
    fun setup() {
        hiltTestRule.inject()

        dao = database.favoritesDao()
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    // Insert to items to database and check if insert ids are returned.
    @Test
    fun a_save_movie_or_show_into_favorites_list_success(): Unit = runBlocking {
        var insertionId = dao.insertFavorite(MockCacheData.movie_1)
        assertEquals(1, insertionId)

        insertionId = dao.insertFavorite(MockCacheData.movie_2)
        assertEquals(2, insertionId)

        insertionId = dao.insertFavorite(MockCacheData.movie_3)
        assertEquals(3, insertionId)
    }

    @Test
    fun b_insert_and_fetch_all_movies_or_shows_from_favorites_list_success(): Unit = runBlocking {
        // Insert the first item.
        var insertionId = dao.insertFavorite(MockCacheData.movie_1)
        assertEquals(1, insertionId)

        // Insert the second item.
        insertionId = dao.insertFavorite(MockCacheData.movie_2)
        assertEquals(2, insertionId)

        // Fetch the newly inserted items.
        val favorites = dao.getAllFavorites()

        // Make sure those items are actually existed.
        assert(favorites.isNotEmpty())

        // Obtain the second item and make sure its data matching that inserted item.
        val item = favorites[1]
        assertEquals(item.id, MockCacheData.movie_2.id)
    }

    @Test
    fun c_remove_one_movie_or_show_from_favorites_list_success(): Unit = runBlocking {
        // Obtain item id.
        val id = MockCacheData.movie_2.id

        // Remove it from the database.
        dao.removeFromFavorites(id)

        // Make sure that item is not existed anymore.
        val favorite = dao.getFavorite(id)
        assert(favorite == null)
    }
}