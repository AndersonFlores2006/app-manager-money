package com.example.gestor_money.domain.usecase

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.graphics.Paint
import android.graphics.Typeface
import com.example.gestor_money.data.repository.TransactionRepository
import com.example.gestor_money.domain.model.TransactionType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
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

            // Calculate totals
            var totalIncome = 0.0
            var totalExpense = 0.0
            transactions.forEach { transaction ->
                if (transaction.type == TransactionType.INCOME) {
                    totalIncome += transaction.amount
                } else {
                    totalExpense += transaction.amount
                }
            }
            val balance = totalIncome - totalExpense

            // Create PDF
            val fileName = "transacciones_${System.currentTimeMillis()}.pdf"
            val file = File(context.filesDir, fileName)

            val pdfDocument = PdfDocument()
            val paint = Paint()
            val titlePaint = Paint()
            val headerPaint = Paint()
            val dataPaint = Paint()
            val linePaint = Paint()

            // Paints setup
            titlePaint.textSize = 24f
            titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            titlePaint.color = android.graphics.Color.BLACK

            headerPaint.textSize = 14f
            headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            headerPaint.color = android.graphics.Color.BLACK

            dataPaint.textSize = 12f
            dataPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            dataPaint.color = android.graphics.Color.BLACK

            linePaint.strokeWidth = 1f
            linePaint.color = android.graphics.Color.BLACK

            val headerBgPaint = Paint().apply {
                color = android.graphics.Color.LTGRAY
                style = android.graphics.Paint.Style.FILL
            }

            // Create page
            val pageInfo = PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas
            val margin = 20f
            var y = 50f

            // Title
            canvas.drawText("Reporte de Transacciones", margin, y, titlePaint)
            y += 40f

            // Table start
            val tableLeft = margin
            val tableRight = 575f
            val col1Width = 120f
            val col2Width = 80f
            val col3Width = 200f
            val col4Width = 80f
            val rowHeight = 25f

            // Header row
            canvas.drawRect(tableLeft, y - 18, tableRight, y + 5, headerBgPaint) // Header background
            canvas.drawText("Fecha", tableLeft + 5, y, headerPaint)
            canvas.drawText("Tipo", tableLeft + col1Width + 5, y, headerPaint)
            canvas.drawText("Descripción", tableLeft + col1Width + col2Width + 5, y, headerPaint)
            canvas.drawText("Monto", tableLeft + col1Width + col2Width + col3Width + 5, y, headerPaint)
            y += rowHeight

            // Data rows
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            transactions.forEach { transaction ->
                // Draw row borders
                canvas.drawLine(tableLeft, y - rowHeight + 10, tableRight, y - rowHeight + 10, linePaint) // Top
                canvas.drawLine(tableLeft, y + 10, tableRight, y + 10, linePaint) // Bottom
                canvas.drawLine(tableLeft + col1Width, y - rowHeight + 10, tableLeft + col1Width, y + 10, linePaint) // Col1
                canvas.drawLine(tableLeft + col1Width + col2Width, y - rowHeight + 10, tableLeft + col1Width + col2Width, y + 10, linePaint) // Col2
                canvas.drawLine(tableLeft + col1Width + col2Width + col3Width, y - rowHeight + 10, tableLeft + col1Width + col2Width + col3Width, y + 10, linePaint) // Col3

                val type = if (transaction.type == TransactionType.INCOME) "Ingreso" else "Gasto"
                canvas.drawText(dateFormat.format(Date(transaction.date)), tableLeft + 5, y, dataPaint)
                canvas.drawText(type, tableLeft + col1Width + 5, y, dataPaint)
                canvas.drawText(transaction.description.take(25), tableLeft + col1Width + col2Width + 5, y, dataPaint) // Truncate long descriptions
                canvas.drawText(transaction.amount.toString(), tableLeft + col1Width + col2Width + col3Width + 5, y, dataPaint)
                y += rowHeight

                if (y > 750) { // New page if needed
                    pdfDocument.finishPage(page)
                    val newPage = pdfDocument.startPage(pageInfo)
                    y = 50f
                }
            }

            // Totals section
            y += 20f
            canvas.drawText("Resumen:", margin, y, headerPaint)
            y += 30f
            canvas.drawText("Total Ingresos: S/ ${String.format("%.2f", totalIncome)}", margin, y, dataPaint)
            y += 20f
            canvas.drawText("Total Gastos: S/ ${String.format("%.2f", totalExpense)}", margin, y, dataPaint)
            y += 20f
            canvas.drawText("Balance: S/ ${String.format("%.2f", balance)}", margin, y, headerPaint)

            pdfDocument.finishPage(page)

            // Save to file
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
