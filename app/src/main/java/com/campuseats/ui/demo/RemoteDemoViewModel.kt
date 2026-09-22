package com.campuseats.ui.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.dao.VendorDao
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.data.network.demo.client.NetworkResult
import com.campuseats.data.network.demo.model.RemoteCampusDataResponse
import com.campuseats.data.network.demo.repository.RemoteDemoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Available demonstration presets for reviewer and testing convenience.
 */
enum class DemoPreset(val label: String, val url: String, val description: String) {
    PRIMARY_GITHUB(
        label = "Primary Live (GitHub Raw)",
        url = RemoteDemoRepository.PRIMARY_LIVE_ENDPOINT,
        description = "Public live HTTPS endpoint serving real cafeteria stalls and dishes."
    ),
    LOCAL_EMULATOR(
        label = "Local Host (10.0.2.2:3000)",
        url = RemoteDemoRepository.LOCAL_EMULATOR_ENDPOINT,
        description = "Hits host Vite/Express server at port 3000 from Android Emulator."
    ),
    MIRROR_CDN(
        label = "CDN Mirror (jsDelivr)",
        url = RemoteDemoRepository.MIRROR_CDN_ENDPOINT,
        description = "High-availability content delivery network mirror."
    ),
    SIMULATED_500(
        label = "Test HTTP 500 Error",
        url = RemoteDemoRepository.SIMULATED_ERROR_500_ENDPOINT,
        description = "Simulates server crash to demonstrate graceful error handling & Room safety."
    ),
    SIMULATED_OFFLINE(
        label = "Test Offline / DNS Error",
        url = RemoteDemoRepository.SIMULATED_OFFLINE_ENDPOINT,
        description = "Simulates network disconnection to demonstrate fallback to local Room data."
    ),
    CUSTOM(
        label = "Custom HTTP URL",
        url = "",
        description = "Input any custom HTTP or HTTPS endpoint for manual testing."
    )
}

/**
 * ViewModel managing the Remote Data Demo lifecycle, HTTP dispatch, and states.
 */
class RemoteDemoViewModel(
    private val repository: RemoteDemoRepository = RemoteDemoRepository(),
    private val vendorDao: VendorDao? = null
) : ViewModel() {

    private val _networkState = MutableStateFlow<NetworkResult<RemoteCampusDataResponse>>(NetworkResult.Idle)
    val networkState: StateFlow<NetworkResult<RemoteCampusDataResponse>> = _networkState.asStateFlow()

    private val _selectedPreset = MutableStateFlow(DemoPreset.PRIMARY_GITHUB)
    val selectedPreset: StateFlow<DemoPreset> = _selectedPreset.asStateFlow()

    private val _targetUrl = MutableStateFlow(DemoPreset.PRIMARY_GITHUB.url)
    val targetUrl: StateFlow<String> = _targetUrl.asStateFlow()

    // Local Room vendors count for explicit proof that Room remains unaffected
    val localRoomVendors: StateFlow<List<VendorEntity>> = vendorDao?.getAllVendors()
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        ?: MutableStateFlow(emptyList())

    /**
     * Updates selected preset and synchronizes the target URL.
     */
    fun selectPreset(preset: DemoPreset) {
        _selectedPreset.value = preset
        if (preset != DemoPreset.CUSTOM) {
            _targetUrl.value = preset.url
        }
    }

    /**
     * Updates custom URL text input.
     */
    fun setCustomUrl(url: String) {
        _selectedPreset.value = DemoPreset.CUSTOM
        _targetUrl.value = url
    }

    /**
     * Executes the asynchronous HTTP GET request on [Dispatchers.IO] via [RemoteDemoRepository].
     */
    fun executeFetch() {
        val url = _targetUrl.value.trim()
        if (url.isBlank()) {
            _networkState.value = NetworkResult.Error(
                message = "URL cannot be empty. Please enter or select a valid HTTP/HTTPS endpoint.",
                statusCode = null,
                isNetworkFailure = false,
                endpointUrl = url
            )
            return
        }

        viewModelScope.launch {
            _networkState.value = NetworkResult.Loading
            val result = repository.fetchRemoteCampusData(url)
            _networkState.value = result
        }
    }

    /**
     * Resets network demonstration back to the initial Idle state.
     */
    fun resetToIdle() {
        _networkState.value = NetworkResult.Idle
    }
}
