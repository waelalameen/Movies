package tech.wa.moviessample.data.repository.details

import kotlinx.coroutines.flow.Flow
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.domain.Details

interface DetailsRepository {

    suspend fun getDetails(id: String): Flow<UiState<Details>>
}