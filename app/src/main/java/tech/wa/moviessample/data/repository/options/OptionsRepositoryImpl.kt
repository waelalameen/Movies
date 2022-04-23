package tech.wa.moviessample.data.repository.options

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.wa.moviessample.data.Resource
import tech.wa.moviessample.data.cache.daos.FavoritesDao
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.cache.entities.Hidden
import tech.wa.moviessample.domain.Favorites
import javax.inject.Inject

class OptionsRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val hiddenDao: HiddenDao
): OptionsRepository {

    override suspend fun getAllFavorites(): Flow<Resource<List<Favorites>>> = flow {
        emit(Resource.Loading(isLoading = true))

        favoritesDao.getAllFavorites().map { it.toFavorite() }.also {
            if (it.isNotEmpty()) {
                emit(Resource.Success(it))
            } else {
                emit(Resource.Loading(isLoading = false))
            }
        }
    }

    override suspend fun getFavorite(id: String): Flow<Resource<Favorites>> = flow {
        emit(Resource.Loading(isLoading = true))

        favoritesDao.getFavorite(id)?.toFavorite().also {
            if (it != null) {
                emit(Resource.Success(it))
            } else {
                emit(Resource.Loading(isLoading = false))
            }
        }
    }

    override suspend fun addToFavorites(favorite: Favorites) {
        favoritesDao.insertFavorite(favorite.toFavoriteDto())
    }

    override suspend fun removeFromFavorites(id: String) {
        favoritesDao.removeFromFavorites(id)
    }

    override suspend fun hideMovie(movie: Hidden) {
        hiddenDao.insertHidden(movie)
    }

    override suspend fun getAllHiddenMovies(): List<Hidden> = hiddenDao.getAllHidden()
}