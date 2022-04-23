package tech.wa.moviessample.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.wa.moviessample.databinding.FragmentMoviesBinding
import tech.wa.moviessample.domain.Search
import tech.wa.moviessample.presenation.ItemInteractionListener
import tech.wa.moviessample.presenation.MoviesViewModel
import tech.wa.moviessample.presenation.RetryEvent
import tech.wa.moviessample.ui.utils.EspressoIdlingResource
import javax.inject.Inject

@AndroidEntryPoint
class MoviesFragment: Fragment(), ItemInteractionListener<Search> {

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

        setupRecycler()
        addSearchInputListener()
        addClearInputListener()
        setupList()
    }

    private fun setupRecycler() {
        moviesAdapter = MoviesAdapter(this)

        EspressoIdlingResource.increment()

        binding.recyclerView.apply {
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
                EspressoIdlingResource.decrement()
            }
        }
    }

    override fun onItemClick(item: Search) {
        val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(item.id)
        Navigation.findNavController(binding.root).navigate(action)
    }
}