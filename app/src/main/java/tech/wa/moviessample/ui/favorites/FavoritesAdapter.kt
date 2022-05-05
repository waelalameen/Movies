package tech.wa.moviessample.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.wa.moviessample.databinding.ItemFavoriteBinding
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.presentation.ItemInteractionListener
import tech.wa.moviessample.ui.utils.FavortiesComparator
import tech.wa.moviessample.ui.utils.MoviesComparator

class FavoritesAdapter (private val itemInteractionListener: FavoriteItemInteractionListener<Favorites>):
    ListAdapter<Favorites, FavoritesAdapter.FavoritesViewHolder>(FavortiesComparator) {

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

        fun bind(item: Favorites) {
            binding.setVariable(BR.item, item)
            binding.setVariable(BR.itemInteractionListener, itemInteractionListener)
            binding.executePendingBindings()
        }
    }
}