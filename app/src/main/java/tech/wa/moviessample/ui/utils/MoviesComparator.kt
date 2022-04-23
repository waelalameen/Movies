package tech.wa.moviessample.ui.utils

import androidx.recyclerview.widget.DiffUtil
import tech.wa.moviessample.domain.Search

object MoviesComparator : DiffUtil.ItemCallback<Search>() {
    override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
        return oldItem == newItem
    }
}