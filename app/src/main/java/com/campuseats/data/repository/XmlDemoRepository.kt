package com.campuseats.data.repository

import android.content.Context
import com.campuseats.R
import com.campuseats.data.xml.CampusFoodXmlParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository responsible for loading and parsing XML campus data.
 * Completely isolated from Room SQLite repositories.
 */
class XmlDemoRepository(
    private val context: Context? = null,
    private val xmlParser: CampusFoodXmlParser = CampusFoodXmlParser()
) {

    suspend fun loadAndParseXml(customXml: String? = null): CampusFoodXmlParser.ParseResult =
        withContext(Dispatchers.IO) {
            if (!customXml.isNullOrBlank()) {
                return@withContext xmlParser.parse(customXml)
            }

            // Attempt to load from Android XML resource
            val resourceXml = loadXmlFromAndroidResources()
            if (resourceXml != null) {
                return@withContext xmlParser.parse(resourceXml)
            }

            // Fallback to embedded default XML template
            return@withContext xmlParser.parse(CampusFoodXmlParser.DEFAULT_SAMPLE_XML)
        }

    private fun loadXmlFromAndroidResources(): String? {
        if (context == null) return null
        return try {
            // Check res/xml or res/raw
            val resId = context.resources.getIdentifier("campus_food_data", "xml", context.packageName)
            if (resId != 0) {
                val parser = context.resources.getXml(resId)
                // If getXml succeeded, we can read the raw text or reconstruct
                // However, reading raw resource stream directly is cleaner for full raw text:
                val rawId = context.resources.getIdentifier("campus_food_data", "raw", context.packageName)
                if (rawId != 0) {
                    context.resources.openRawResource(rawId).bufferedReader().use { it.readText() }
                } else {
                    // Read from assets or fallback to default template
                    CampusFoodXmlParser.DEFAULT_SAMPLE_XML
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
