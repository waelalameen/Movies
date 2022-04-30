package tech.wa.moviessample.presentation

import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.domain.Hidden

sealed class RetryEvent {
    object Idle: RetryEvent()
    data class SearchEvent(val query: String): RetryEvent()
    data class DetailsEvent(val id: String): RetryEvent()
    object GetAllFavoritesEvent: RetryEvent()
    data class AddToFavoritesEvent(val favorites: Favorites): RetryEvent()
    data class RemoveFromFavorites(val id: String): RetryEvent()
    data class HideMovie(val movie: Hidden): RetryEvent()
}
