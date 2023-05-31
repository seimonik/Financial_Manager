package com.example.financialmanager.utils

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun TextInputEditText.transformIntoDatePicker(
    context: Context,
    format: String,
    maxDate: Date? = null,
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDataSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val sdf = SimpleDateFormat(format)
        setText(sdf.format(myCalendar.time))
    }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerOnDataSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also {
                datePicker.maxDate = it
                show()
            }
        }
    }
}

inline fun View.snack(
    @StringRes string: Int,
    length: Int = Snackbar.LENGTH_LONG,
    action: Snackbar.() -> Unit = {}
) {
    val snack = Snackbar.make(this, resources.getString(string), length)
    action.invoke(snack)
    snack.show()
}

fun parseDouble(value: String?): Double {
    return if (value == null || value.isEmpty()) Double.NaN else value.toDouble()
}

fun createImageFileFromUri(context: Context, imageUri: Uri, albumName: String): File? {
    val inputStream = context.contentResolver.openInputStream(imageUri)
    return when(inputStream) {
        null -> null
        else -> {
            createFileFromInputStream(context, inputStream, albumName)
        }
    }
}

fun createFileFromInputStream(context: Context, inputStream: InputStream, albumName: String): File? {
    try {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val appDir = context.filesDir.path
        val fullDir = appDir + File.separator + albumName
        val directory = File(fullDir)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val imageFile = File(fullDir, fileName)
        imageFile.setWritable(true, false)

        val outputStream: OutputStream = FileOutputStream(imageFile)
        val buffer = ByteArray(1024)
        var length = 0
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()

        return imageFile
    }
    catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}