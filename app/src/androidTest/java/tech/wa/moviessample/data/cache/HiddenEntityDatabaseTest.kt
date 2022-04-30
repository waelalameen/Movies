package tech.wa.moviessample.data.cache

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.di.CacheModule
import javax.inject.Inject
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@UninstallModules(CacheModule::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@HiltAndroidTest
class HiddenEntityDatabaseTest {

    @get:Rule
    var hiltTestRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MoviesDatabase

    private lateinit var dao: HiddenDao

    @Before
    fun setup() {
        hiltTestRule.inject()

        dao = database.hiddenDao()
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    // Insert to items to database and check if insert ids are returned.
    @Test
    fun a_save_movie_or_show_into_hidden_list_success(): Unit = runBlocking {
        var insertionId = dao.insertHidden(MockCacheData.hidden_movie_1)
        assertEquals(1, insertionId)

        insertionId = dao.insertHidden(MockCacheData.hidden_movie_2)
        assertEquals(2, insertionId)
    }

    @Test
    fun b_insert_and_fetch_all_movies_or_shows_from_hidden_list_success(): Unit = runBlocking {
        // Insert an item.
        val insertionId = dao.insertHidden(MockCacheData.hidden_movie_2)
        assertEquals(1, insertionId)

        // Fetch the newly inserted items.
        val hiddenList = dao.getAllHidden()

        // Make sure those items are actually existed.
        assert(hiddenList.isNotEmpty())

        // Obtain the second item and make sure its data matching that inserted item.
        val item = hiddenList[0]
        assertEquals(item.id, MockCacheData.hidden_movie_2.id)
    }
}