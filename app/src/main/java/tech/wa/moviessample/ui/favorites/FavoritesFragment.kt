package tech.wa.moviessample.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.wa.moviessample.R
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.databinding.FragmentFavoritesBinding
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.extensions.pop
import tech.wa.moviessample.extensions.visible
import tech.wa.moviessample.presentation.MoviesViewModel

@AndroidEntryPoint
class FavoritesFragment: Fragment(), FavoriteItemInteractionListener<Favorites> {

    private lateinit var binding: FragmentFavoritesBinding

    private val favoritesAdapter: FavoritesAdapter = FavoritesAdapter(this)

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

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.visible(true)

        setupRecycler()

        viewModel.getAllFavorites()

        binding.backButtonCardView.setOnClickListener { pop() }
    }

    private fun setupRecycler() {
        binding.favoritesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                updateUi()
            }
        }
    }

    private suspend fun updateUi() {
        viewModel.favoritesState.collectLatest { uiState ->
            when (uiState.state) {
                UiState.State.SUCCESS -> {
                    val favorites = uiState.data ?: emptyList()
                    favoritesAdapter.submitList(favorites)
                }
                else -> Unit
            }
        }
    }

    override fun onItemClick(item: Favorites) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToMovieDetailsFragment2(item.id)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun removeFromFavorites(item: Favorites) {
        viewModel.removeFromFavorites(item.id)
        viewModel.getAllFavorites()
    }
}