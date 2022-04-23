package tech.wa.moviessample.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.wa.moviessample.R
import tech.wa.moviessample.databinding.ItemMovieStateBinding
import tech.wa.moviessample.extensions.visible

class MoviesLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<MoviesLoadStateAdapter.MoviesLoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MoviesLoadStateViewHolder {
        return MoviesLoadStateViewHolder(
            ItemMovieStateBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie_state, parent, false)
            ),
            retry = retry
        )
    }

    override fun onBindViewHolder(holder: MoviesLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class MoviesLoadStateViewHolder(
        private val binding: ItemMovieStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.textViewError.text = loadState.error.localizedMessage
            }

            binding.progressbar.visible(loadState is LoadState.Loading)
            binding.buttonRetry.visible(loadState is LoadState.Error)
            binding.textViewError.visible(loadState is LoadState.Error)
            binding.buttonRetry.setOnClickListener {
                retry()
            }
        }
    }
}