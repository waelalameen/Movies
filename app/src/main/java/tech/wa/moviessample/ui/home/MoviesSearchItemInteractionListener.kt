package tech.wa.moviessample.ui.home

import android.widget.CompoundButton
import tech.wa.moviessample.presentation.ItemInteractionListener

interface MoviesSearchItemInteractionListener<T>: ItemInteractionListener<T> {

    fun addToFavorite(view: CompoundButton, item: T, checked: Boolean)

    fun hideFromFeed(item: T)
}