package tech.wa.moviessample.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tech.wa.moviessample.R
import tech.wa.moviessample.databinding.FragmentDetailsBinding
import tech.wa.moviessample.extensions.pop
import tech.wa.moviessample.extensions.visible
import tech.wa.moviessample.presentation.MoviesViewModel

@AndroidEntryPoint
class MovieDetailsFragment: Fragment() {

    private lateinit var binding: FragmentDetailsBinding

    private val viewModel: MoviesViewModel by viewModels()

    private val arguments: MovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.visible(false)

        arguments.id?.let {
            viewModel.getDetails(it)
        }

        binding.also {
            it.lifecycleOwner = viewLifecycleOwner
            it.setVariable(BR.viewModel, viewModel)
            it.executePendingBindings()
        }

        binding.backButtonCardView.setOnClickListener { pop() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}