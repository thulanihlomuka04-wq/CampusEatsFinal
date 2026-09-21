package com.campuseats.multimedia

/**
 * Utility for managing image resources, placeholder banners, food thumbnails,
 * and campus vendor avatars without requiring external bulky third-party dependencies.
 */
object ImageLoaderUtils {
    /**
     * Category default icon indicators or placeholder resource identifiers.
     */
    fun getCategoryPlaceholder(category: String): String {
        return when (category.lowercase()) {
            "burgers", "fast food" -> "burger"
            "drinks", "beverages", "coffee" -> "coffee"
            "healthy", "salads" -> "salad"
            "bakery", "pastry" -> "bakery"
            "pizza" -> "pizza"
            else -> "meal"
        }
    }
}
