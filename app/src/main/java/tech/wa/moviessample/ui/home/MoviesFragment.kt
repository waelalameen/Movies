package tech.wa.moviessample.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_LONG
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.wa.moviessample.R
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.databinding.FragmentMoviesBinding
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.presentation.MoviesViewModel
import tech.wa.moviessample.ui.utils.EspressoIdlingResource

@AndroidEntryPoint
class MoviesFragment: Fragment(), MoviesSearchItemInteractionListener<Search> {

    private lateinit var binding: FragmentMoviesBinding

    private lateinit var moviesAdapter: MoviesAdapter

    private val viewModel: MoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(inflater)
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
        addSearchInputListener()
        addClearInputListener()
        setupList()
    }

    private fun setupRecycler() {
        moviesAdapter = MoviesAdapter(this)

        EspressoIdlingResource.increment()

        binding.searchResultsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moviesAdapter.withLoadStateFooter(
                footer = MoviesLoadStateAdapter {
                    moviesAdapter.retry()
                }
            )
        }
    }

    private fun addSearchInputListener() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(chars: CharSequence?, p1: Int, p2: Int, p3: Int) {
                chars?.let {
                    lifecycleScope.launch {
                        viewModel.search(query = it.toString())
                        moviesAdapter.refresh()
                        delay(500)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun addClearInputListener() {
        binding.searchInputLayout.setEndIconOnClickListener {
            binding.searchInput.setText("")
            moviesAdapter.refresh()
        }
    }

    private fun setupList() {
        lifecycleScope.launch {
            viewModel.searchResults.collectLatest { pagingData ->
                moviesAdapter.submitData(pagingData)
                moviesAdapter.loadStateFlow.collectLatest {
                    when (it.refresh) {
                        is LoadState.Loading -> {
                            if (!LoadState.Loading.endOfPaginationReached) {
                                viewModel.searchResultsStatus.value = UiState.Success(data = pagingData)
                            } else {
                                viewModel.searchResultsStatus.value = UiState.Loading()
                            }
                        }
                        is LoadState.Error -> viewModel.searchResultsStatus.value = UiState.Loading()
                        else -> {}
                    }
                }
                EspressoIdlingResource.decrement()
            }
        }
    }

    override fun onItemClick(item: Search) {
        val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(item.id)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun addToFavorite(item: Search) {
        Snackbar.make(binding.root, getString(R.string.add_to_favorites_message), Snackbar.LENGTH_LONG).show()
        viewModel.addToFavorites(item.toFavorite())
    }

    override fun hideFromFeed(item: Search) {
        Snackbar.make(binding.root, getString(R.string.hide_confirmation_message), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.confirm)) {
                viewModel.hideMovie(item.toHidden())
            }
            .show()
    }
}