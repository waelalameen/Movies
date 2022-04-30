package tech.wa.moviessample.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.wa.moviessample.databinding.FragmentFavoritesBinding
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.extensions.pop
import tech.wa.moviessample.presentation.MoviesViewModel
import tech.wa.moviessample.ui.home.MoviesLoadStateAdapter

@AndroidEntryPoint
class FavoritesFragment: Fragment(), FavoriteItemInteractionListener<Favorites> {

    private lateinit var binding: FragmentFavoritesBinding

    private lateinit var favoritesAdapter: FavoritesAdapter

    private val viewModel: MoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.also {
            it.lifecycleOwner = viewLifecycleOwner
            it.setVariable(BR.viewModel, viewModel)
            it.executePendingBindings()
        }

        setupRecycler()

        viewModel.getAllFavorites()

        binding.backButtonCardView.setOnClickListener { pop() }
    }

    private fun setupRecycler() {
        favoritesAdapter = FavoritesAdapter()

        binding.favoritesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter.withLoadStateFooter(
                footer = MoviesLoadStateAdapter {
                    favoritesAdapter.retry()
                }
            )
        }
    }

    override fun removeFromFavorites(item: Favorites) {
        viewModel.removeFromFavorites(item.id)
    }
}