package com.example.gestor_money.domain.usecase

import android.content.Context
import com.example.gestor_money.data.repository.TransactionRepository
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
    suspend operator fun invoke(): Result<File> {
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
                row.createCell(1).setCellValue(if (transaction.type == "INCOME") "Ingreso" else "Gasto")
                row.createCell(2).setCellValue(transaction.description)
                row.createCell(3).setCellValue(transaction.amount)
            }
            
            // Auto-size columns
            headers.indices.forEach { sheet.autoSizeColumn(it) }
            
            // Save file
            val fileName = "transacciones_${System.currentTimeMillis()}.xlsx"
            val file = File(context.getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
