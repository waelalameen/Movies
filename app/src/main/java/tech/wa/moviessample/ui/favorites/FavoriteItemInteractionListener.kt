package tech.wa.moviessample.ui.favorites

import tech.wa.moviessample.presentation.ItemInteractionListener

interface FavoriteItemInteractionListener<T>: ItemInteractionListener<T> {

    fun removeFromFavorites(item: T)
}