package tech.wa.moviessample.data.repository.options

import kotlinx.coroutines.flow.Flow
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.domain.Hidden
import tech.wa.moviessample.domain.Search

interface OptionsRepository {

    suspend fun getAllFavorites(): Flow<UiState<List<Favorites>>>

    suspend fun getFavorite(id: String): Flow<UiState<Favorites>>

    suspend fun addToFavorites(favorite: Favorites)

    suspend fun removeFromFavorites(id: String)

    suspend fun hideMovieOrShow(movie: Hidden)

    suspend fun getHiddenMoviesOrShows(): Flow<List<Search>>
}