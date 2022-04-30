package tech.wa.moviessample.presentation

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.wa.moviessample.data.Constants.DEFAULT_PAGE_SIZE
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.data.repository.details.DetailsRepository
import tech.wa.moviessample.data.repository.options.OptionsRepository
import tech.wa.moviessample.data.repository.search.SearchDataSource
import tech.wa.moviessample.domain.Details
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.domain.Hidden
import tech.wa.moviessample.domain.Search
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val api: MoviesApi,
    private val detailsRepository: DetailsRepository,
    private val optionsRepository: OptionsRepository
) : ViewModel(), Observable {

    private val registry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        registry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        registry.remove(callback)
    }

    private val hidden: MutableSharedFlow<List<Search>> = MutableSharedFlow()

    private fun loadHiddenMoviesOrShows() {
        viewModelScope.launch {
            optionsRepository.getHiddenMoviesOrShows().onEach {
                hidden.emit(it)
            }.launchIn(viewModelScope)
        }
    }

    @get:Bindable
    val searchResultsStatus = MutableStateFlow<UiState<PagingData<Search>>>(UiState.Idle())

    val searchResults = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
        pagingSourceFactory = {
            loadHiddenMoviesOrShows()
            SearchDataSource(api, queryState.value)
        }
    ).flow.filterNot { it == hidden }
        .cachedIn(viewModelScope)

    private val queryState = MutableStateFlow("s")

    fun search(query: String) {
        queryState.value = query
        _retryEvent.value = RetryEvent.SearchEvent(query)
        registry.notifyChange(this, BR.searchResultsStatus)
    }

    private val _detailsState =
        MutableStateFlow<UiState<Details>>(UiState.Loading(isLoading = false))

    @get:Bindable
    val detailsState: StateFlow<UiState<Details>> = _detailsState

    fun getDetails(id: String) {
        viewModelScope.launch {
            detailsRepository.getDetails(id).onEach {
                _detailsState.value = it
                _retryEvent.value = RetryEvent.DetailsEvent(id)
                registry.notifyChange(this@MoviesViewModel, BR.detailsState)
            }.launchIn(this)
        }
    }

    fun hideMovie(movie: Hidden) {
        viewModelScope.launch {
            optionsRepository.hideMovieOrShow(movie)
            _retryEvent.value = RetryEvent.HideMovie(movie)
        }
    }

    fun addToFavorites(favorites: Favorites) {
        viewModelScope.launch {
            optionsRepository.addToFavorites(favorites)
            _retryEvent.value = RetryEvent.AddToFavoritesEvent(favorites)
        }
    }

    fun removeFromFavorites(id: String) {
        viewModelScope.launch {
            optionsRepository.removeFromFavorites(id)
            _retryEvent.value = RetryEvent.RemoveFromFavorites(id)
        }
    }

    private val _favoritesState =
        MutableStateFlow<UiState<List<Favorites>>>(UiState.Loading(isLoading = false))

    @get:Bindable
    val favoritesState: StateFlow<UiState<List<Favorites>>> = _favoritesState

    fun getAllFavorites() {
        viewModelScope.launch {
            optionsRepository.getAllFavorites().onEach {
                _favoritesState.value = it
                _retryEvent.value = RetryEvent.GetAllFavoritesEvent
                registry.notifyChange(this@MoviesViewModel, BR.favoritesState)
            }.launchIn(this)
        }
    }

    private val _retryEvent = MutableStateFlow<RetryEvent>(RetryEvent.Idle)

    private val retryEvent: StateFlow<RetryEvent> = _retryEvent

    fun retry() {
        when (retryEvent.value) {
            is RetryEvent.SearchEvent -> {
                val event = retryEvent.value as RetryEvent.SearchEvent
                search(event.query)
            }
            is RetryEvent.DetailsEvent -> {
                val event = retryEvent.value as RetryEvent.DetailsEvent
                getDetails(event.id)
            }
            is RetryEvent.GetAllFavoritesEvent -> getAllFavorites()
            is RetryEvent.AddToFavoritesEvent -> {
                val event = retryEvent.value as RetryEvent.AddToFavoritesEvent
                addToFavorites(event.favorites)
            }
            is RetryEvent.HideMovie -> {
                val event = retryEvent.value as RetryEvent.HideMovie
                hideMovie(event.movie)
            }
            else -> {
                val event = retryEvent.value as RetryEvent.RemoveFromFavorites
                removeFromFavorites(event.id)
            }
        }
    }
}