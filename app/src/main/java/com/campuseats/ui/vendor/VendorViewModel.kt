package com.campuseats.ui.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.OrderWithItems
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.data.repository.VendorRepository
import com.campuseats.security.SessionManager
import com.campuseats.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class VendorViewModel(
    private val vendorRepository: VendorRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val allVendors: StateFlow<List<VendorEntity>> = vendorRepository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVendorId = MutableStateFlow<String?>(null)

    val currentVendorId: StateFlow<String> = combine(
        _selectedVendorId,
        allVendors,
        sessionManager.currentUserId
    ) { selectedId, vendors, userId ->
        if (selectedId != null && vendors.any { it.id == selectedId }) {
            selectedId
        } else if (userId != null && userId.startsWith("user_vendor_") && userId != "user_vendor_primary" && userId != "user_vendor_1") {
            val derivedId = userId.removePrefix("user_vendor_")
            if (vendors.any { it.id == derivedId }) derivedId else (vendors.firstOrNull()?.id ?: "vendor_1")
        } else {
            val userName = sessionManager.getUserName()
            val matchingVendor = vendors.find {
                it.name.contains(userName ?: "", ignoreCase = true) ||
                (userName?.contains(it.name, ignoreCase = true) == true)
            }
            matchingVendor?.id ?: (vendors.firstOrNull()?.id ?: "vendor_1")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "vendor_1")

    val vendorName: StateFlow<String?> = sessionManager.currentUserName

    val vendorDetails: Flow<VendorEntity?> = currentVendorId.flatMapLatest { vendorId ->
        vendorRepository.getVendorDetails(vendorId)
    }

    val foodItems: StateFlow<List<FoodItemEntity>> = currentVendorId.flatMapLatest { vendorId ->
        vendorRepository.getFoodItemsForVendor(vendorId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = currentVendorId.flatMapLatest { vendorId ->
        vendorRepository.getOrdersForVendor(vendorId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ordersWithItems: StateFlow<List<OrderWithItems>> = currentVendorId.flatMapLatest { vendorId ->
        vendorRepository.getOrdersWithItemsForVendor(vendorId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun selectVendor(vendorId: String) {
        _selectedVendorId.value = vendorId
    }

    fun addFoodItem(
        name: String,
        description: String,
        price: Double,
        category: String,
        isVegetarian: Boolean,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            when (val res = vendorRepository.addFoodItem(
                vendorId = currentVendorId.value,
                name = name,
                description = description,
                price = price,
                category = category,
                isVegetarian = isVegetarian,
                imageUrl = imageUrl
            )) {
                is Resource.Success -> _actionMessage.value = "Food item added successfully!"
                is Resource.Error -> _actionMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun editFoodItem(
        item: FoodItemEntity,
        name: String,
        description: String,
        price: Double,
        category: String,
        isAvailable: Boolean,
        isVegetarian: Boolean,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            val updated = item.copy(
                name = name.trim(),
                description = description.trim(),
                price = price,
                category = category.trim(),
                isAvailable = isAvailable,
                isVegetarian = isVegetarian,
                imageUrl = imageUrl?.trim()?.ifBlank { null }
            )
            when (val res = vendorRepository.updateFoodItem(updated)) {
                is Resource.Success -> _actionMessage.value = "Menu item updated successfully!"
                is Resource.Error -> _actionMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun toggleAvailability(item: FoodItemEntity) {
        viewModelScope.launch {
            vendorRepository.toggleFoodAvailability(item)
        }
    }

    fun deleteFoodItem(item: FoodItemEntity) {
        viewModelScope.launch {
            when (val res = vendorRepository.deleteFoodItem(item)) {
                is Resource.Success -> _actionMessage.value = "${item.name} removed from menu."
                is Resource.Error -> _actionMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            when (val res = vendorRepository.updateOrderStatus(orderId, newStatus)) {
                is Resource.Success -> _actionMessage.value = "Order updated to ${newStatus.getDisplayName()}"
                is Resource.Error -> _actionMessage.value = res.message
                else -> Unit
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}

