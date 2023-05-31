package com.example.financialmanager.presentation.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.dateToString(format: String): String {
    val dateFormatter = SimpleDateFormat(format, Locale("ru"))
    return dateFormatter.format(this)
}