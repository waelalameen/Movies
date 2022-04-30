package tech.wa.moviessample.ui.home

import tech.wa.moviessample.presentation.ItemInteractionListener

interface MoviesSearchItemInteractionListener<T>: ItemInteractionListener<T> {

    fun addToFavorite(item: T)

    fun hideFromFeed(item: T)
}