package tech.wa.moviessample.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import tech.wa.moviessample.databinding.FragmentFavoritesBinding
import tech.wa.moviessample.extensions.pop
import tech.wa.moviessample.ui.home.MoviesLoadStateAdapter

class FavoritesFragment: Fragment() {

    private lateinit var binding: FragmentFavoritesBinding

    private lateinit var favoritesAdapter: FavoritesAdapter

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

        setupRecycler()

        binding.backButtonCardView.setOnClickListener { pop() }
    }

    private fun setupRecycler() {
        favoritesAdapter = FavoritesAdapter()

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter.withLoadStateFooter(
                footer = MoviesLoadStateAdapter {
                    favoritesAdapter.retry()
                }
            )
        }
    }
}