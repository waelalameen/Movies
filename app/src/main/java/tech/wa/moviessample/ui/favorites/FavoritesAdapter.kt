package tech.wa.moviessample.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.wa.moviessample.databinding.ItemFavoriteBinding
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.ui.utils.MoviesComparator

class FavoritesAdapter: PagingDataAdapter<Search, FavoritesAdapter.FavoritesViewHolder>(MoviesComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class FavoritesViewHolder(private val binding: ItemFavoriteBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(searchItem: Search) {
            binding.setVariable(BR.item, searchItem)
            binding.executePendingBindings()
        }
    }
}