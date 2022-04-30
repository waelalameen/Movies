package tech.wa.moviessample.presentation

interface ItemInteractionListener<T> {

    fun onItemClick(item: T) { }
}