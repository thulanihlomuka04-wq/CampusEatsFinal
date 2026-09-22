package com.campuseats.ui.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.repository.XmlDemoRepository
import com.campuseats.data.xml.CampusFoodXmlParser
import com.campuseats.data.xml.XmlCampusDining
import com.campuseats.data.xml.XmlFoodItem
import com.campuseats.data.xml.XmlParseMetrics
import com.campuseats.data.xml.XmlVendor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class XmlDisplayMode {
    PARSED_CARDS,
    RAW_XML_INSPECTOR,
    PARSER_TELEMETRY
}

data class XmlDemoUiState(
    val isLoading: Boolean = false,
    val diningData: XmlCampusDining? = null,
    val parseMetrics: XmlParseMetrics? = null,
    val rawXml: String = "",
    val errorMessage: String? = null,
    val selectedCategoryFilter: String? = null,
    val displayMode: XmlDisplayMode = XmlDisplayMode.PARSED_CARDS,
    val isCustomXmlEdited: Boolean = false
) {
    val categories: List<String>
        get() {
            val cats = mutableSetOf<String>()
            diningData?.vendors?.forEach { vendor ->
                vendor.menuItems.forEach { item ->
                    if (item.category.isNotBlank()) cats.add(item.category)
                }
            }
            return cats.sorted()
        }

    val filteredVendors: List<XmlVendor>
        get() {
            val vendors = diningData?.vendors ?: return emptyList()
            if (selectedCategoryFilter.isNullOrBlank()) return vendors

            return vendors.mapNotNull { vendor ->
                val matchingItems = vendor.menuItems.filter {
                    it.category.equals(selectedCategoryFilter, ignoreCase = true)
                }
                if (matchingItems.isNotEmpty()) {
                    vendor.copy(menuItems = matchingItems)
                } else null
            }
        }
}

class XmlDemoViewModel(
    private val repository: XmlDemoRepository = XmlDemoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(XmlDemoUiState(isLoading = true))
    val uiState: StateFlow<XmlDemoUiState> = _uiState.asStateFlow()

    init {
        loadXml()
    }

    fun loadXml(customXml: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = repository.loadAndParseXml(customXml)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        diningData = result.data,
                        parseMetrics = result.metrics,
                        rawXml = result.rawXml,
                        errorMessage = null,
                        isCustomXmlEdited = customXml != null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "XML Parsing Failed: ${e.localizedMessage ?: e.message}"
                    )
                }
            }
        }
    }

    fun setCategoryFilter(category: String?) {
        _uiState.update {
            it.copy(selectedCategoryFilter = if (it.selectedCategoryFilter == category) null else category)
        }
    }

    fun setDisplayMode(mode: XmlDisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
    }

    fun resetToDefault() {
        loadXml(null)
    }

    fun injectTestItem() {
        // Appends a new item into the XML to visually prove that the parser is dynamically reading and processing the XML
        val currentXml = _uiState.value.rawXml
        if (currentXml.contains("</menuItems>")) {
            val newItemXml = """
                <item id="ITEM-DYNAMIC-${System.currentTimeMillis() % 1000}" vegetarian="true">
                    <name>Dynamic Chef Special #${(10..99).random()}</name>
                    <category>Specials</category>
                    <price>39.95</price>
                    <calories>340</calories>
                    <allergens>None</allergens>
                    <description>Dynamically injected XML item parsed live at ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}.</description>
                </item>
            """.trimIndent()

            val modifiedXml = currentXml.replaceFirst("</menuItems>", "$newItemXml\n            </menuItems>")
            loadXml(modifiedXml)
        }
    }
}
