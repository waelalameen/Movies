package tech.wa.moviessample.ui.utils

import androidx.recyclerview.widget.DiffUtil
import tech.wa.moviessample.domain.Favorites

object FavortiesComparator : DiffUtil.ItemCallback<Favorites>() {
    override fun areItemsTheSame(oldItem: Favorites, newItem: Favorites): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Favorites, newItem: Favorites): Boolean {
        return oldItem == newItem
    }
}