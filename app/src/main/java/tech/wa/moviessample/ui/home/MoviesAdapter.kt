package tech.wa.moviessample.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.wa.moviessample.databinding.ItemMovieBinding
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.presentation.ItemInteractionListener
import tech.wa.moviessample.ui.utils.MoviesComparator

class MoviesAdapter(private val itemInteractionListener: ItemInteractionListener<Search>)
    : PagingDataAdapter<Search, MoviesAdapter.MoviesViewHolder>(MoviesComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class MoviesViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(searchItem: Search) {
            binding.setVariable(BR.item, searchItem)
            binding.setVariable(BR.itemInteractionListener, itemInteractionListener)
            binding.executePendingBindings()
        }
    }
}