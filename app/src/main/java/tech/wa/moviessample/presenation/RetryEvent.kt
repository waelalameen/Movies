package tech.wa.moviessample.presenation

import tech.wa.moviessample.domain.Favorites

sealed class RetryEvent {
    data class SearchEvent(val query: String): RetryEvent()
    data class DetailsEvent(val id: String): RetryEvent()
    object GetAllFavoritesEvent: RetryEvent()
    data class AddToFavoritesEvent(val favorites: Favorites): RetryEvent()
    data class RemoveFromFavorites(val id: String): RetryEvent()
}
