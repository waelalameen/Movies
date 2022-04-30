package tech.wa.moviessample.data

sealed class UiState<out T>(
    val state: State = State.LOADING,
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T?) : UiState<T>(state = State.SUCCESS, data = data)
    class Error<T>(errorMessage: String?) :
        UiState<T>(state = State.ERROR, errorMessage = errorMessage)
    class Loading<T>(isLoading: Boolean = false) :
        UiState<T>(state = State.LOADING, isLoading = isLoading)
    class Idle<T>: UiState<T>(state = State.IDLE, isLoading = false)

    enum class State {
        LOADING, SUCCESS, ERROR, IDLE
    }
}


