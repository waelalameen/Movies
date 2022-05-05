package tech.wa.moviessample.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.wa.moviessample.R
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.databinding.FragmentMoviesBinding
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.extensions.hideKeyboard
import tech.wa.moviessample.extensions.visible
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

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.visible(true)

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
            // itemAnimator = null
            adapter = moviesAdapter.withLoadStateFooter(
                footer = MoviesLoadStateAdapter {
                    moviesAdapter.retry()
                }
            )
        }
    }

    private fun addSearchInputListener() {
        binding.searchInput.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text.toString().trim()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requireActivity().hideKeyboard(binding.root)

                if (text.isNotBlank()) {
                    lifecycleScope.launch {
                        viewModel.search(query = text)
                        setupList()
                        moviesAdapter.refresh()
                    }
                }
            }
            false
        }
    }

    private fun addClearInputListener() {
        binding.searchInputLayout.setEndIconOnClickListener {
            binding.searchInput.setText("")
            requireActivity().hideKeyboard(binding.root)

            lifecycleScope.launch {
                moviesAdapter.refresh()
                viewModel.cleanUp()
            }
        }
    }

    private fun setupList() {
        lifecycleScope.launch {
            viewModel.searchResults.collectLatest { pagingData ->
                moviesAdapter.submitData(pagingData)
                moviesAdapter.loadStateFlow.collect {
                    println("refresh => ${it.refresh}")
                    collectState(it, pagingData)
                }

                EspressoIdlingResource.decrement()
            }
        }
    }

    private fun collectState(state: CombinedLoadStates, pagingData: PagingData<Search>) {
        when (state.refresh) {
            is LoadState.Loading -> {
                viewModel.searchResultsState.value = UiState.Loading()
            }
            is LoadState.NotLoading -> {
                if (viewModel.queryState.value.isBlank()) {
                    viewModel.searchResultsState.value = UiState.Idle()
                } else {
                    viewModel.searchResultsState.value = UiState.Success(data = pagingData)
                }
            }
            is LoadState.Error -> viewModel.searchResultsState.value = UiState.Error(
                errorMessage = getString(R.string.emptyMessage),
                errorIcon = R.drawable.illustration_error
            )
            else -> viewModel.searchResultsState.value = UiState.Idle()
        }
    }

    override fun onItemClick(item: Search) {
        val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(item.id)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun addToFavorite(view: CompoundButton, item: Search, checked: Boolean) {
        if (!view.isPressed) return

        val index = moviesAdapter.snapshot().indexOf(item)

        if (checked) {
            item.isFavorite = true
            moviesAdapter.notifyItemChanged(index, item)
            viewModel.addToFavorites(item.toFavorite())
        } else {
            item.isFavorite = false
            moviesAdapter.notifyItemChanged(index, item)
            viewModel.removeFromFavorites(item.id)
        }

        showFavoritesSnackMessage(checked)
    }

    private fun showFavoritesSnackMessage(checked: Boolean) {
        val message = if (checked) {
            getString(R.string.add_to_favorites_message)
        } else {
            getString(R.string.remove_from_favorites_message)
        }

        val rootView = binding.coordinatorLayout
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

    override fun hideFromFeed(item: Search) {
        val rootView = binding.coordinatorLayout
        Snackbar.make(rootView.findViewById(R.id.coordinator_layout),
            getString(R.string.hide_confirmation_message), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.confirm)) {
                //viewModel.hideMovie(item.toHidden())
            }
            .show()
    }
}