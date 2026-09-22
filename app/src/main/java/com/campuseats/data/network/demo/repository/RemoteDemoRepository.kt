package com.campuseats.data.network.demo.repository

import com.campuseats.data.network.demo.client.NetworkResult
import com.campuseats.data.network.demo.client.RemoteDemoHttpClient
import com.campuseats.data.network.demo.model.RemoteCampusDataResponse

/**
 * ============================================================================
 * REMOTE DEMO REPOSITORY (SEPARATE FROM ROOM REPOSITORY LAYER)
 * ============================================================================
 *
 * This repository is dedicated exclusively to demonstration and external HTTP
 * synchronization. It intentionally does NOT depend on [AppDatabase] or Room DAOs,
 * enforcing architectural isolation:
 * - If a remote network request fails, Room local data is unaffected.
 * - The student and vendor workflows remain 100% offline-first and functional.
 */
class RemoteDemoRepository(
    private val httpClient: RemoteDemoHttpClient = RemoteDemoHttpClient()
) {

    /**
     * Preset endpoints for live testing, mirroring, and error simulation.
     */
    companion object {
        /**
         * Primary live endpoint hosted on GitHub Raw.
         * Resolves reliably via HTTPS and serves formatted vendor/menu JSON.
         */
        const val PRIMARY_LIVE_ENDPOINT =
            "https://raw.githubusercontent.com/thulanihlomuka/campuseats-demo/main/campus_vendors_demo.json"

        /**
         * Local dev server endpoint (when testing with Android Emulator connecting to host).
         * 10.0.2.2 is the special Android Emulator alias for host machine localhost.
         */
        const val LOCAL_EMULATOR_ENDPOINT =
            "http://10.0.2.2:3000/demo-vendors.json"

        /**
         * Secondary mirror endpoint on jsDelivr CDN.
         */
        const val MIRROR_CDN_ENDPOINT =
            "https://cdn.jsdelivr.net/gh/thulanihlomuka/campuseats-demo@main/campus_vendors_demo.json"

        /**
         * Simulated error endpoint: HTTP 500 Internal Server Error.
         * Used to demonstrate robust UI error handling when the remote server fails.
         */
        const val SIMULATED_ERROR_500_ENDPOINT =
            "https://httpstat.us/500"

        /**
         * Simulated error endpoint: HTTP 404 Not Found.
         */
        const val SIMULATED_ERROR_404_ENDPOINT =
            "https://httpstat.us/404"

        /**
         * Simulated DNS failure / offline domain.
         */
        const val SIMULATED_OFFLINE_ENDPOINT =
            "https://invalid-campus-eats-offline-domain-test.org/api"
    }

    /**
     * Executes the HTTP request and returns a [NetworkResult].
     */
    suspend fun fetchRemoteCampusData(endpointUrl: String): NetworkResult<RemoteCampusDataResponse> {
        return httpClient.getRemoteCampusData(endpointUrl)
    }
}
