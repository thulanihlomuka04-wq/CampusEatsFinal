package com.campuseats.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.data.repository.AdminRepository
import com.campuseats.security.SessionManager
import com.campuseats.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val adminName: StateFlow<String?> = sessionManager.currentUserName

    val allUsers: StateFlow<List<UserEntity>> = adminRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVendors: StateFlow<List<VendorEntity>> = adminRepository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = adminRepository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCount: StateFlow<Int> = adminRepository.getUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val vendorCount: StateFlow<Int> = adminRepository.getVendorCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val orderCount: StateFlow<Int> = adminRepository.getTotalOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRevenue: StateFlow<Double?> = adminRepository.getTotalRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            when (val res = adminRepository.deleteUser(userId)) {
                is Resource.Success -> _statusMessage.value = "User deleted successfully."
                is Resource.Error -> _statusMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun addVendor(
        name: String,
        description: String,
        location: String,
        hours: String,
        vendorEmail: String? = null,
        vendorPasswordPlain: String? = null
    ) {
        viewModelScope.launch {
            when (val res = adminRepository.addVendor(name, description, location, hours, vendorEmail, vendorPasswordPlain)) {
                is Resource.Success -> _statusMessage.value = "Vendor registered successfully."
                is Resource.Error -> _statusMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun toggleVendorStatus(vendor: VendorEntity) {
        viewModelScope.launch {
            adminRepository.toggleVendorStatus(vendor)
        }
    }

    fun deleteVendor(vendor: VendorEntity) {
        viewModelScope.launch {
            adminRepository.deleteVendor(vendor)
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
