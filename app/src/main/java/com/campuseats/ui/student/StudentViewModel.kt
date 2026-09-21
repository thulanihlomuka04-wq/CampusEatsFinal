package com.campuseats.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderItemEntity
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.data.repository.CartItem
import com.campuseats.data.repository.StudentRepository
import com.campuseats.security.SessionManager
import com.campuseats.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrderPlacementState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val placedOrder: OrderEntity? = null
)

class StudentViewModel(
    private val studentRepository: StudentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val studentName: StateFlow<String?> = sessionManager.currentUserName
    val studentId: StateFlow<String?> = sessionManager.currentUserId

    val vendors: StateFlow<List<VendorEntity>> = studentRepository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableFoodCounts: StateFlow<Map<String, Int>> = studentRepository.getAvailableFoodCounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val cartItems: StateFlow<List<CartItem>> = studentRepository.cartItems

    private val _orderPlacementState = MutableStateFlow(OrderPlacementState())
    val orderPlacementState: StateFlow<OrderPlacementState> = _orderPlacementState.asStateFlow()

    fun getMenuForVendor(vendorId: String): Flow<List<FoodItemEntity>> {
        return studentRepository.getMenuForVendor(vendorId)
    }

    suspend fun getVendor(vendorId: String): VendorEntity? {
        return studentRepository.getVendorById(vendorId)
    }

    fun getStudentOrders(): Flow<List<OrderEntity>> {
        val currentId = sessionManager.currentUserId.value ?: ""
        return studentRepository.getStudentOrders(currentId)
    }

    fun getActiveOrder(): Flow<OrderEntity?> {
        val currentId = sessionManager.currentUserId.value ?: ""
        return studentRepository.getStudentOrders(currentId).map { orders ->
            orders.firstOrNull { it.status != OrderStatus.COLLECTED && it.status != OrderStatus.REJECTED }
        }
    }

    fun getOrderById(orderId: String): Flow<OrderEntity?> {
        return studentRepository.getOrderById(orderId)
    }

    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> {
        return studentRepository.getOrderItems(orderId)
    }

    fun addToCart(item: FoodItemEntity) {
        studentRepository.addToCart(item)
    }

    fun removeFromCart(itemId: String) {
        studentRepository.removeFromCart(itemId)
    }

    fun updateCartQuantity(itemId: String, quantity: Int) {
        studentRepository.updateQuantity(itemId, quantity)
    }

    fun clearCart() {
        studentRepository.clearCart()
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }

    fun placeOrder(notes: String) {
        val currentUserId = sessionManager.currentUserId.value ?: ""
        val currentUserName = sessionManager.currentUserName.value ?: "Student"

        viewModelScope.launch {
            _orderPlacementState.value = OrderPlacementState(isLoading = true)
            when (val result = studentRepository.placeOrder(currentUserId, currentUserName, notes)) {
                is Resource.Success -> {
                    _orderPlacementState.value = OrderPlacementState(placedOrder = result.data)
                }
                is Resource.Error -> {
                    _orderPlacementState.value = OrderPlacementState(errorMessage = result.message)
                }
                is Resource.Loading -> {
                    _orderPlacementState.value = OrderPlacementState(isLoading = true)
                }
            }
        }
    }

    fun resetOrderPlacementState() {
        _orderPlacementState.value = OrderPlacementState()
    }
}
