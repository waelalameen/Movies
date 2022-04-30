package tech.wa.moviessample.data.repository.options

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.domain.Hidden
import tech.wa.moviessample.domain.Search
import javax.inject.Inject

class OptionsRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val hiddenDao: HiddenDao
): OptionsRepository {

    @WorkerThread
    override suspend fun getAllFavorites(): Flow<UiState<List<Favorites>>> = flow<UiState<List<Favorites>>> {
        emit(UiState.Loading(isLoading = true))

        favoritesDao.getAllFavorites().map { it.toFavorite() }.also {
            if (it.isNotEmpty()) {
                emit(UiState.Success(it))
            } else {
                emit(UiState.Idle())
            }
        }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    override suspend fun getFavorite(id: String): Flow<UiState<Favorites>> = flow<UiState<Favorites>> {
        emit(UiState.Loading(isLoading = true))

        favoritesDao.getFavorite(id)?.toFavorite().also {
            if (it != null) {
                emit(UiState.Success(it))
            } else {
                emit(UiState.Idle())
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addToFavorites(favorite: Favorites) {
        favoritesDao.insertFavorite(favorite.toFavoriteDto())
    }

    override suspend fun removeFromFavorites(id: String) {
        favoritesDao.removeFromFavorites(id)
    }

    override suspend fun hideMovieOrShow(movie: Hidden) {
        hiddenDao.insertHidden(movie.toHiddenEntity())
    }

    @WorkerThread
    override suspend fun getHiddenMoviesOrShows(): Flow<List<Search>> = flow {
        emit(hiddenDao.getAllHidden().map { it.toSearchResult() })
    }.flowOn(Dispatchers.IO)
}