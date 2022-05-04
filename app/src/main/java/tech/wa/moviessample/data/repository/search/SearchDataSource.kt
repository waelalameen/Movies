package tech.wa.moviessample.data.repository.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import tech.wa.moviessample.data.cache.daos.HiddenDao
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.domain.Search

class SearchDataSource(
    private val api: MoviesApi,
    private val query: String
) : PagingSource<Int, Search>() {

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? = state.anchorPosition

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
                prevKey = null, // if (nextPageNumber > 0) nextPageNumber - 1 else
                nextKey = if (nextPageNumber < totalPages) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}