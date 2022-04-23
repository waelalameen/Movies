package tech.wa.moviessample.presenation

interface ItemInteractionListener<T> {
    fun onItemClick(item: T)
}