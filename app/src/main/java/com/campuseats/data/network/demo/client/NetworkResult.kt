package com.campuseats.data.network.demo.client

/**
 * Sealed hierarchy representing the state of an asynchronous network request.
 * Encapsulates loading, success with metadata, and structured error states.
 */
sealed class NetworkResult<out T> {

    /**
     * Initial idle state prior to dispatching any network request.
     */
    data object Idle : NetworkResult<Nothing>()

    /**
     * Active asynchronous execution state (progress spinner displayed).
     */
    data object Loading : NetworkResult<Nothing>()

    /**
     * Successful response state.
     *
     * @param data The deserialized response object
     * @param statusCode HTTP response status code (e.g. 200 OK)
     * @param durationMs Measured round-trip network latency in milliseconds
     * @param rawJson Raw unparsed JSON string payload for auditing/debugging
     * @param endpointUrl The exact URL requested
     */
    data class Success<out T>(
        val data: T,
        val statusCode: Int,
        val durationMs: Long,
        val rawJson: String,
        val endpointUrl: String
    ) : NetworkResult<T>()

    /**
     * Error response state capturing either HTTP protocol errors (4xx, 5xx)
     * or transport failures (DNS resolution, timeout, offline).
     *
     * @param message Human-readable error description
     * @param statusCode HTTP status code if a response was received (null on connection failure)
     * @param isNetworkFailure True if the device was offline or connection timed out
     * @param endpointUrl The target URL that produced the error
     */
    data class Error(
        val message: String,
        val statusCode: Int? = null,
        val isNetworkFailure: Boolean = false,
        val endpointUrl: String = ""
    ) : NetworkResult<Nothing>()
}
