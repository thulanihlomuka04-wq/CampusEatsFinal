package com.campuseats.data.network.demo.model

/**
 * ============================================================================
 * CAMPUS EATS REMOTE DEMO DATA MODELS & JSON SCHEMA SPECIFICATION
 * ============================================================================
 *
 * This file defines the data transfer objects (DTOs) for the Remote Demo
 * Data feature. This layer is completely separated from the Room SQLite entities
 * (UserEntity, VendorEntity, FoodItemEntity, OrderEntity) to ensure that the core
 * ordering system remains 100% resilient and offline-first.
 *
 * Expected HTTP JSON Response Structure:
 * {
 *   "status": "success",
 *   "campus": "Main Campus Food Village",
 *   "generatedAt": "2026-09-22T10:30:00Z",
 *   "vendorCount": 2,
 *   "endpointDocumentation": "Campus Eats Real HTTP REST demonstration endpoint",
 *   "vendors": [
 *     {
 *       "id": "remote-v1",
 *       "name": "The Daily Roast & Artisan Bakery",
 *       "location": "Student Commons, 2nd Floor",
 *       "openingHours": "07:30 - 18:00",
 *       "rating": 4.8,
 *       "isOpen": true,
 *       "description": "Specialty espresso bar, handcrafted cold brews...",
 *       "menuItems": [
 *         {
 *           "id": "remote-f1",
 *           "name": "Caramel Macchiato",
 *           "description": "Double espresso shot layered with steamed milk...",
 *           "price": 38.50,
 *           "category": "BEVERAGES",
 *           "isVegetarian": true,
 *           "calories": 240,
 *           "isAvailable": true
 *         }
 *       ]
 *     }
 *   ]
 * }
 */

/**
 * Top-level response returned by the remote HTTP endpoint.
 */
data class RemoteCampusDataResponse(
    val status: String,
    val campus: String,
    val generatedAt: String,
    val vendorCount: Int,
    val endpointDocumentation: String,
    val vendors: List<RemoteVendorDto>
)

/**
 * Remote vendor / cafeteria stall model parsed from JSON.
 */
data class RemoteVendorDto(
    val id: String,
    val name: String,
    val location: String,
    val openingHours: String,
    val rating: Double,
    val isOpen: Boolean,
    val description: String,
    val menuItems: List<RemoteFoodItemDto>
)

/**
 * Remote menu / food item model parsed from nested JSON array.
 */
data class RemoteFoodItemDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val isVegetarian: Boolean,
    val calories: Int,
    val isAvailable: Boolean
)
