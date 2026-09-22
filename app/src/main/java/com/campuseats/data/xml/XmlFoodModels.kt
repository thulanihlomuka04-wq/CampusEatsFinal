package com.campuseats.data.xml

/**
 * Clean Kotlin data objects representing parsed XML entities.
 * Decoupled completely from Room SQLite database tables.
 */

data class XmlCampusMetadata(
    val title: String = "",
    val curriculumTopic: String = "",
    val academicYear: String = "",
    val semester: String = ""
)

data class XmlFoodItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val calories: Int,
    val isVegetarian: Boolean,
    val allergens: String,
    val description: String
)

data class XmlVendor(
    val id: String,
    val name: String,
    val category: String,
    val status: String,
    val building: String,
    val rating: Double,
    val openingHours: String,
    val acceptsStudentCard: Boolean,
    val contactEmail: String,
    val menuItems: List<XmlFoodItem> = emptyList()
)

data class XmlCampusDining(
    val campus: String = "",
    val version: String = "",
    val generatedDate: String = "",
    val source: String = "",
    val metadata: XmlCampusMetadata = XmlCampusMetadata(),
    val vendors: List<XmlVendor> = emptyList()
) {
    val totalFoodItems: Int
        get() = vendors.sumOf { it.menuItems.size }
}

data class XmlParseMetrics(
    val durationMs: Long,
    val tagsEncountered: Int,
    val attributesProcessed: Int,
    val rawXmlBytes: Int,
    val parserEngine: String = "org.xmlpull.v1.XmlPullParser (Android Native)"
)
