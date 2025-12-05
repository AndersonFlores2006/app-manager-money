package com.example.gestor_money.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.gestor_money.data.repository.TransactionRepository
import com.example.gestor_money.domain.model.TransactionType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExportToExcelUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): Result<Uri> {
        return try {
            val transactions = transactionRepository.getAllTransactions().first()
            
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Transacciones")
            
            // Create header style
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                val font = workbook.createFont().apply {
                    bold = true
                }
                setFont(font)
            }
            
            // Create header row
            val headerRow = sheet.createRow(0)
            val headers = listOf("Fecha", "Tipo", "Descripción", "Monto")
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }
            
            // Fill data
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            transactions.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(dateFormat.format(Date(transaction.date)))
                row.createCell(1).setCellValue(if (transaction.type == TransactionType.INCOME) "Ingreso" else "Gasto")
                row.createCell(2).setCellValue(transaction.description)
                row.createCell(3).setCellValue(transaction.amount)
            }
            
            // Auto-size columns
            headers.indices.forEach { sheet.autoSizeColumn(it) }
            
            // Save file
            val fileName = "transacciones_${System.currentTimeMillis()}.xlsx"
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for API 29+
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val insertUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                insertUri?.let { uri ->
                    try {
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            workbook.write(outputStream)
                        }
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                        uri
                    } catch (e: Exception) {
                        resolver.delete(uri, null, null)
                        throw e
                    }
                } ?: throw Exception("No se pudo crear el archivo en Downloads")
            } else {
                // Use legacy method for older APIs
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(dir, fileName)
                FileOutputStream(file).use { outputStream ->
                    workbook.write(outputStream)
                }
                Uri.fromFile(file)
            }

            try {
                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            } finally {
                workbook.close()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
