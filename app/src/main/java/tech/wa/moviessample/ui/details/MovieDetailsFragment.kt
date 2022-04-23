package tech.wa.moviessample.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tech.wa.moviessample.databinding.FragmentDetailsBinding
import tech.wa.moviessample.extensions.pop
import tech.wa.moviessample.presenation.MoviesViewModel

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

        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()

        arguments.id?.let {
            viewModel.getDetails(it)
        }

        binding.backButtonCardView.setOnClickListener { pop() }
    }
}