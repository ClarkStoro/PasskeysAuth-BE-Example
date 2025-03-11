package utils


object DefaultError {
    const val ERROR_KEY = "error"
    const val DEFAULT_ERROR_MESSAGE = "An unexpected error occurred"
}

fun Exception.getDefaultError() =
    mapOf(DefaultError.ERROR_KEY to (this.message ?: DefaultError.DEFAULT_ERROR_MESSAGE))