package tech.wa.moviessample.presenation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.wa.moviessample.data.Constants.DEFAULT_PAGE_SIZE
import tech.wa.moviessample.data.Resource
import tech.wa.moviessample.data.cache.entities.FavoritesDto
import tech.wa.moviessample.data.cache.entities.Hidden
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.data.repository.details.DetailsRepository
import tech.wa.moviessample.data.repository.options.OptionsRepository
import tech.wa.moviessample.data.repository.search.SearchDataSource
import tech.wa.moviessample.domain.Details
import tech.wa.moviessample.domain.Favorites
import tech.wa.moviessample.domain.Search
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val api: MoviesApi,
    private val detailsRepository: DetailsRepository,
    private val optionsRepository: OptionsRepository
) : ViewModel() {

    private val hidden: PagingData<Search>
        get() {
            val items = arrayListOf<Hidden>()
            viewModelScope.launch {
                items.addAll(optionsRepository.getAllHiddenMovies())
            }
            return PagingData.from(items.map { it.toSearchResult() })
        }

    val searchStatus = MutableStateFlow<Resource<PagingData<Search>>>(Resource.Success(null))

    val searchResults = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
        pagingSourceFactory = {
            SearchDataSource(api, queryState.value)
        }
    ).flow.filterNot { it == hidden }
        .cachedIn(viewModelScope)

    private val queryState = MutableStateFlow("s")

    fun search(query: String) {
        queryState.value = query
    }

    private val _detailsState = MutableStateFlow<Resource<Details>>(Resource.Loading(isLoading = false))
    val detailsState: StateFlow<Resource<Details>> = _detailsState

    fun getDetails(id: String) {
        viewModelScope.launch {
            detailsRepository.getDetails(id).onEach {
                _detailsState.value = it
            }.launchIn(this)
        }
    }

    fun addToFavorites(favorites: Favorites) {
        viewModelScope.launch {
            optionsRepository.addToFavorites(favorites)
        }
    }

    fun removeFromFavorites(id: String) {
        viewModelScope.launch {
            optionsRepository.removeFromFavorites(id)
        }
    }

    private val _favoritesState = MutableStateFlow<Resource<List<Favorites>>>(Resource.Loading(isLoading = false))

    val favoritesState: StateFlow<Resource<List<Favorites>>> = _favoritesState

    fun getAllFavorites() {
        viewModelScope.launch {
            optionsRepository.getAllFavorites().onEach {
                _favoritesState.value = it
            }.launchIn(this)
        }
    }

    fun retry() {
//        when (event) {
//            is RetryEvent.SearchEvent -> search(event.query)
//            is RetryEvent.DetailsEvent -> getDetails(event.id)
//        }
    }
}