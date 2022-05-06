package tech.wa.moviessample.presentation

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
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

    init {
        loadHiddenMoviesOrShows()
    }

    private val hidden = arrayListOf<Search>()

    private fun loadHiddenMoviesOrShows() {
        viewModelScope.launch {
            optionsRepository.getHiddenMoviesOrShows().onEach {
                hidden.addAll(it)
                println("hidden => $it")
            }.launchIn(viewModelScope)
        }
    }

    @get:Bindable
    val searchResultsState = MutableStateFlow<UiState<PagingData<Search>>>(UiState.Idle())

    val queryState = MutableStateFlow("")

    fun search(query: String) {
        queryState.value = query
        _retryEvent.value = RetryEvent.SearchEvent(query)
        searchResultsState.value = UiState.Loading(isLoading = true)
        registry.notifyChange(this, BR.searchResultsState)
    }

    fun cleanUp() {
        queryState.value = ""
        searchResultsState.value = UiState.Idle()
        registry.notifyChange(this, BR.searchResultsState)
    }

    val searchResults = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
        pagingSourceFactory = {
            SearchDataSource(api, queryState.value, searchResultsState)
        }
    ).flow
        .map { pagingData ->
            pagingData.map {
                val favoriteItem = optionsRepository.getFavorite(it.id).last().data
                val item = favoriteItem?.toSearch()?.copy(isFavorite = true) ?: it
                item
            }
        }
        .map { pagingData ->
            pagingData.filter { it.id !in hidden.map { item -> item.id } }
        }
        .cachedIn(viewModelScope)

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
            loadHiddenMoviesOrShows()
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