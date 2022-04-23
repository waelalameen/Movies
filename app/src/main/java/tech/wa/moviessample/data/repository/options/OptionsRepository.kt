package tech.wa.moviessample.data.repository.options

import kotlinx.coroutines.flow.Flow
import tech.wa.moviessample.data.Resource
import tech.wa.moviessample.data.cache.entities.FavoritesDto
import tech.wa.moviessample.data.cache.entities.Hidden
import tech.wa.moviessample.domain.Favorites

interface OptionsRepository {

    suspend fun getAllFavorites(): Flow<Resource<List<Favorites>>>

    suspend fun getFavorite(id: String): Flow<Resource<Favorites>>

    suspend fun addToFavorites(favorite: Favorites)

    suspend fun removeFromFavorites(id: String)

    suspend fun hideMovie(movie: Hidden)

    suspend fun getAllHiddenMovies(): List<Hidden>
}