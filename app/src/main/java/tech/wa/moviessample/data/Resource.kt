package tech.wa.moviessample.data

sealed class Resource<out T>(
    val state: State = State.LOADING,
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T?) : Resource<T>(state = State.SUCCESS, data = data)
    class Error<T>(errorMessage: String?) :
        Resource<T>(state = State.ERROR, errorMessage = errorMessage)
    class Loading<T>(isLoading: Boolean = false) :
        Resource<T>(state = State.LOADING, isLoading = isLoading)

    enum class State {
        LOADING, SUCCESS, ERROR
    }
}


