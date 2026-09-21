package com.campuseats.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Currency formatting utility for campus food pricing.
 */
object CurrencyFormatter {
    private val locale = Locale("en", "ZA") // University standard: South African Rand (or adaptable standard currency)

    fun format(amount: Double): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(locale)
            format.format(amount)
        } catch (e: Exception) {
            "R %.2f".format(amount)
        }
    }
}
