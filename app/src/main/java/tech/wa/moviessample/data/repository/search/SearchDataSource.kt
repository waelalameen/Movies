package tech.wa.moviessample.data.repository.search

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.MutableStateFlow
import tech.wa.moviessample.R
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.domain.Search
import java.net.SocketTimeoutException

class SearchDataSource(
    private val api: MoviesApi,
    private val query: String,
    private val state: MutableStateFlow<UiState<PagingData<Search>>> = MutableStateFlow(UiState.Idle())
) : PagingSource<Int, Search>() {

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Search> {
        return try {
            val nextPageNumber = params.key ?: 1

            val response = api.search(query, nextPageNumber)
            val body = response.body()
            body?.let {
                if (it.responseStatus == "False" && it.errorMessage != "Incorrect IMDb ID.") {
                    throw Exception(it.errorMessage)
                }
            }

            val searchResults = body?.searchDtoResults?.map { it.toSearch() } ?: emptyList()
            val totalPages = body?.totalResults?.toInt() ?: 0

            LoadResult.Page(
                data = searchResults,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < totalPages) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            if (e is SocketTimeoutException) {
                state.value = UiState.Error(errorMessage = "No Wifi")
            } else {
                state.value = UiState.Error(
                    errorMessage = e.message,
                    errorIcon = R.drawable.illustration_error
                )
            }

            LoadResult.Error(e)
        }
    }
}