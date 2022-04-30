package tech.wa.moviessample.data.repository.details

import androidx.annotation.WorkerThread
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.wa.moviessample.data.UiState
import tech.wa.moviessample.data.remote.MoviesApi
import tech.wa.moviessample.domain.Details
import javax.inject.Inject

@ViewModelScoped
class DetailsRepositoryImpl @Inject constructor(
    private val api: MoviesApi
) : DetailsRepository {

    @WorkerThread
    override suspend fun getDetails(id: String): Flow<UiState<Details>> = flow {
        emit(UiState.Loading(isLoading = true))
        try {
            val response = api.details(id)

            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.responseStatus == "False") {
                        throw Exception(it.errorMessage)
                    }
                    emit(UiState.Success(data = it.toDetails()))
                }
            } else {
                val stream = response.errorBody()?.charStream()
                val error = stream?.readText()
                emit(UiState.Error(errorMessage = error))
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMessage = e.message))
        }
    }
}