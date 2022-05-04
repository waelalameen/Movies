package tech.wa.moviessample.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast.LENGTH_LONG
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.wa.moviessample.R
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.databinding.FragmentMoviesBinding
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.extensions.hideKeyboard
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
        binding.searchInput.setOnEditorActionListener { textView, actionId, _ ->
            val text = textView.text.toString().trim()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requireActivity().hideKeyboard(binding.root)

                if (text.isNotBlank()) {
                    lifecycleScope.launch {
                        viewModel.search(query = text)
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
            lifecycleScope.launch {
                moviesAdapter.submitData(PagingData.empty())
                moviesAdapter.refresh()
            }
        }
    }

    private fun setupList() {
        //if (binding.searchInput.text.toString().trim().isBlank()) return

        lifecycleScope.launch {
            viewModel.searchResults.collectLatest { pagingData ->
                moviesAdapter.submitData(pagingData)
                moviesAdapter.loadStateFlow.collect {
                    println("refresh => ${it.refresh}")
                    when (it.refresh) {
                        is LoadState.Loading -> {
                            viewModel.searchResultsStatus.value = UiState.Loading()
                        }
                        is LoadState.NotLoading -> {
                            viewModel.searchResultsStatus.value = UiState.Success(data = pagingData)
                        }
                        is LoadState.Error -> viewModel.searchResultsStatus.value = UiState.Error(
                            errorMessage = getString(R.string.emptyMessage)
                        )
                        else -> viewModel.searchResultsStatus.value = UiState.Idle()
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
        val rootView = binding.coordinatorLayout
        Snackbar.make(rootView, getString(R.string.add_to_favorites_message), Snackbar.LENGTH_LONG).show()
        //viewModel.addToFavorites(item.toFavorite())
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