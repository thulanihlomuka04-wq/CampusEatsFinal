package com.campuseats.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.dao.OrderStatusCount
import com.campuseats.data.local.dao.PopularFoodItem
import com.campuseats.data.local.dao.VendorOrderStat
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.data.repository.AdminRepository
import com.campuseats.security.SessionManager
import com.campuseats.security.UserRole
import com.campuseats.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val currentUserId: StateFlow<String?> = sessionManager.currentUserId
    val adminName: StateFlow<String?> = sessionManager.currentUserName

    val allUsers: StateFlow<List<UserEntity>> = adminRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVendors: StateFlow<List<VendorEntity>> = adminRepository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = adminRepository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics for Dashboard & Reports
    val userCount: StateFlow<Int> = adminRepository.getUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val studentCount: StateFlow<Int> = adminRepository.getStudentCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val vendorCount: StateFlow<Int> = adminRepository.getVendorCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val orderCount: StateFlow<Int> = adminRepository.getTotalOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingOrdersCount: StateFlow<Int> = adminRepository.getPendingOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedOrdersCount: StateFlow<Int> = adminRepository.getCompletedOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRevenue: StateFlow<Double?> = adminRepository.getTotalRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOrderValue: StateFlow<Double?> = adminRepository.getTotalOrderValue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val orderStatusCounts: StateFlow<List<OrderStatusCount>> = adminRepository.getOrderStatusCounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ordersPerVendor: StateFlow<List<VendorOrderStat>> = adminRepository.getOrdersPerVendor()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularFoodItems: StateFlow<List<PopularFoodItem>> = adminRepository.getPopularFoodItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodCountsGrouped: StateFlow<Map<String, Int>> = adminRepository.getTotalFoodCountsGrouped()
        .map { list -> list.associate { it.vendorId to it.count } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun toggleUserActive(user: UserEntity) {
        val activeAdminId = sessionManager.getUserId()
        if (user.id == activeAdminId) {
            _statusMessage.value = "Security: You cannot disable your own active administrator account."
            return
        }
        if (user.role == UserRole.ADMIN && user.email.equals("admin@campuseats.com", ignoreCase = true)) {
            _statusMessage.value = "Security: The primary system administrator account cannot be disabled."
            return
        }

        viewModelScope.launch {
            when (val res = adminRepository.toggleUserActiveStatus(user.id, user.isActive)) {
                is Resource.Success -> {
                    _statusMessage.value = if (user.isActive) {
                        "Account for ${user.fullName} has been disabled."
                    } else {
                        "Account for ${user.fullName} has been re-enabled."
                    }
                }
                is Resource.Error -> _statusMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        val activeAdminId = sessionManager.getUserId()
        if (user.id == activeAdminId) {
            _statusMessage.value = "Security: You cannot delete your own active administrator account."
            return
        }
        if (user.role == UserRole.ADMIN && user.email.equals("admin@campuseats.com", ignoreCase = true)) {
            _statusMessage.value = "Security: The primary system administrator account cannot be deleted."
            return
        }

        viewModelScope.launch {
            when (val res = adminRepository.deleteUser(user.id)) {
                is Resource.Success -> _statusMessage.value = "User ${user.fullName} deleted successfully."
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
                is Resource.Success -> _statusMessage.value = "Vendor stall registered successfully."
                is Resource.Error -> _statusMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun updateVendor(
        vendorId: String,
        name: String,
        description: String,
        location: String,
        hours: String
    ) {
        viewModelScope.launch {
            when (val res = adminRepository.updateVendor(vendorId, name, description, location, hours)) {
                is Resource.Success -> _statusMessage.value = "Vendor details updated successfully."
                is Resource.Error -> _statusMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun assignVendorCredentials(
        vendorId: String,
        vendorName: String,
        email: String,
        passwordPlain: String
    ) {
        viewModelScope.launch {
            when (val res = adminRepository.assignVendorCredentials(vendorId, vendorName, email, passwordPlain)) {
                is Resource.Success -> _statusMessage.value = "Vendor credentials successfully assigned/updated."
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
            when (val res = adminRepository.deleteVendor(vendor)) {
                is Resource.Success -> _statusMessage.value = "Vendor ${vendor.name} deleted successfully."
                is Resource.Error -> _statusMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
