package com.campuseats.data.network.demo.client

import com.campuseats.data.network.demo.model.RemoteCampusDataResponse
import com.campuseats.data.network.demo.model.RemoteFoodItemDto
import com.campuseats.data.network.demo.model.RemoteVendorDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

/**
 * ============================================================================
 * LIGHTWEIGHT ANDROID-COMPATIBLE HTTP CLIENT & JSON PARSER
 * ============================================================================
 *
 * This client provides real, asynchronous HTTP networking using the Android
 * platform's native [HttpURLConnection] coupled with [org.json.JSONObject].
 *
 * KEY DEMONSTRATION CHARACTERISTICS:
 * 1. Real HTTP GET Request:
 *    Opens a genuine TCP/TLS socket to the remote server, sends HTTP headers,
 *    and parses HTTP status lines.
 *
 * 2. Asynchronous Execution:
 *    Guaranteed to run on [Dispatchers.IO] to strictly avoid NetworkOnMainThreadException.
 *
 * 3. Manual & Safe JSON Parsing:
 *    Parses arrays and nested objects with field-level type safety and defaults.
 *
 * 4. Comprehensive Error Handling:
 *    Distinguishes DNS failures, socket timeouts, HTTP 4xx/5xx responses,
 *    and malformed JSON syntax.
 *
 * 5. Complete Room Database Isolation:
 *    Failures here NEVER disrupt local Room SQLite operations.
 */
class RemoteDemoHttpClient {

    companion object {
        private const val CONNECT_TIMEOUT_MS = 8000
        private const val READ_TIMEOUT_MS = 8000
        private const val USER_AGENT = "CampusEats-Android/1.0 (MobileComputing2B)"
    }

    /**
     * Executes an asynchronous HTTP GET request to the specified endpoint,
     * reads the response stream, and parses the JSON body into [RemoteCampusDataResponse].
     *
     * @param endpointUrl The complete HTTP/HTTPS URL to request
     * @return [NetworkResult] containing Success with parsed model or Error with diagnostics
     */
    suspend fun getRemoteCampusData(endpointUrl: String): NetworkResult<RemoteCampusDataResponse> =
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL(endpointUrl)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", USER_AGENT)
                    doInput = true
                }

                // Connect to remote host and obtain HTTP response code
                connection.connect()
                val statusCode = connection.responseCode
                val durationMs = System.currentTimeMillis() - startTime

                // Handle HTTP response stream (200 OK vs Error stream)
                val inputStream = if (statusCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }

                reader = BufferedReader(InputStreamReader(inputStream))
                val responseBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    responseBuilder.append(line)
                }
                val rawJson = responseBuilder.toString()

                // Check for non-2xx HTTP error codes
                if (statusCode !in 200..299) {
                    return@withContext NetworkResult.Error(
                        message = "Remote server returned HTTP $statusCode (${connection.responseMessage})",
                        statusCode = statusCode,
                        isNetworkFailure = false,
                        endpointUrl = endpointUrl
                    )
                }

                // Parse JSON payload
                val parsedData = parseCampusDataJson(rawJson)

                NetworkResult.Success(
                    data = parsedData,
                    statusCode = statusCode,
                    durationMs = durationMs,
                    rawJson = rawJson,
                    endpointUrl = endpointUrl
                )

            } catch (e: UnknownHostException) {
                // Device is offline or DNS lookup failed
                NetworkResult.Error(
                    message = "Network Error: Unable to resolve host (${e.message}). Verify device internet connection.",
                    statusCode = null,
                    isNetworkFailure = true,
                    endpointUrl = endpointUrl
                )
            } catch (e: SocketTimeoutException) {
                // Connection or read timeout exceeded
                NetworkResult.Error(
                    message = "Network Timeout: Server did not respond within ${CONNECT_TIMEOUT_MS / 1000} seconds.",
                    statusCode = null,
                    isNetworkFailure = true,
                    endpointUrl = endpointUrl
                )
            } catch (e: FileNotFoundException) {
                // Endpoint returned HTTP 404
                NetworkResult.Error(
                    message = "HTTP 404: The requested remote demo endpoint does not exist at this URL.",
                    statusCode = 404,
                    isNetworkFailure = false,
                    endpointUrl = endpointUrl
                )
            } catch (e: JSONException) {
                // Payload is not valid JSON according to schema
                NetworkResult.Error(
                    message = "JSON Parsing Failure: The remote response did not conform to the expected schema (${e.message}).",
                    statusCode = 200,
                    isNetworkFailure = false,
                    endpointUrl = endpointUrl
                )
            } catch (e: IOException) {
                // General I/O or transport failure
                NetworkResult.Error(
                    message = "I/O Transport Error: ${e.localizedMessage ?: "Connection reset or network unreachable"}",
                    statusCode = null,
                    isNetworkFailure = true,
                    endpointUrl = endpointUrl
                )
            } catch (e: Exception) {
                // Unexpected runtime exception
                NetworkResult.Error(
                    message = "Unexpected Error: ${e.localizedMessage ?: "Unknown client fault"}",
                    statusCode = null,
                    isNetworkFailure = false,
                    endpointUrl = endpointUrl
                )
            } finally {
                try {
                    reader?.close()
                } catch (_: IOException) { }
                connection?.disconnect()
            }
        }

    /**
     * Parses the raw JSON string into strongly-typed [RemoteCampusDataResponse].
     * Uses Android's built-in [JSONObject] and [JSONArray].
     */
    @Throws(JSONException::class)
    private fun parseCampusDataJson(jsonString: String): RemoteCampusDataResponse {
        val root = JSONObject(jsonString)

        val status = root.optString("status", "success")
        val campus = root.optString("campus", "Main Campus")
        val generatedAt = root.optString("generatedAt", "")
        val vendorCount = root.optInt("vendorCount", 0)
        val endpointDoc = root.optString("endpointDocumentation", "Campus Eats Demonstration Endpoint")

        val vendorsList = mutableListOf<RemoteVendorDto>()
        val vendorsArray = root.optJSONArray("vendors") ?: JSONArray()

        for (i in 0 until vendorsArray.length()) {
            val vendorObj = vendorsArray.getJSONObject(i)
            val vendorId = vendorObj.optString("id", "remote-$i")
            val vendorName = vendorObj.optString("name", "Unknown Stall")
            val location = vendorObj.optString("location", "Campus Center")
            val openingHours = vendorObj.optString("openingHours", "08:00 - 17:00")
            val rating = vendorObj.optDouble("rating", 4.5)
            val isOpen = vendorObj.optBoolean("isOpen", true)
            val description = vendorObj.optString("description", "")

            val menuItemsList = mutableListOf<RemoteFoodItemDto>()
            val menuArray = vendorObj.optJSONArray("menuItems") ?: JSONArray()

            for (j in 0 until menuArray.length()) {
                val itemObj = menuArray.getJSONObject(j)
                menuItemsList.add(
                    RemoteFoodItemDto(
                        id = itemObj.optString("id", "food-$j"),
                        name = itemObj.optString("name", "Menu Item"),
                        description = itemObj.optString("description", ""),
                        price = itemObj.optDouble("price", 0.0),
                        category = itemObj.optString("category", "GENERAL"),
                        isVegetarian = itemObj.optBoolean("isVegetarian", false),
                        calories = itemObj.optInt("calories", 0),
                        isAvailable = itemObj.optBoolean("isAvailable", true)
                    )
                )
            }

            vendorsList.add(
                RemoteVendorDto(
                    id = vendorId,
                    name = vendorName,
                    location = location,
                    openingHours = openingHours,
                    rating = rating,
                    isOpen = isOpen,
                    description = description,
                    menuItems = menuItemsList
                )
            )
        }

        return RemoteCampusDataResponse(
            status = status,
            campus = campus,
            generatedAt = generatedAt,
            vendorCount = if (vendorCount > 0) vendorCount else vendorsList.size,
            endpointDocumentation = endpointDoc,
            vendors = vendorsList
        )
    }
}
