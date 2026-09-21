package com.campuseats.ui.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.data.repository.VendorRepository
import com.campuseats.security.SessionManager
import com.campuseats.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VendorViewModel(
    private val vendorRepository: VendorRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Default vendor association for active vendor session
    val currentVendorId: String = "vendor_1"
    val vendorName: StateFlow<String?> = sessionManager.currentUserName

    val vendorDetails: Flow<VendorEntity?> = vendorRepository.getVendorDetails(currentVendorId)

    val foodItems: StateFlow<List<FoodItemEntity>> = vendorRepository.getFoodItemsForVendor(currentVendorId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = vendorRepository.getOrdersForVendor(currentVendorId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    fun addFoodItem(
        name: String,
        description: String,
        price: Double,
        category: String,
        isVegetarian: Boolean
    ) {
        viewModelScope.launch {
            when (val res = vendorRepository.addFoodItem(currentVendorId, name, description, price, category, isVegetarian)) {
                is Resource.Success -> _actionMessage.value = "Food item added successfully!"
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
            vendorRepository.deleteFoodItem(item)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            vendorRepository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
