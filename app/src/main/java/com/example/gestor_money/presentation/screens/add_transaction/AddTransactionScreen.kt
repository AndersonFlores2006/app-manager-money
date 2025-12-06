package com.example.gestor_money.presentation.screens.add_transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val availableCategories = categories.filter { it.type == uiState.type.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Transacción") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Selector
            Text(
                text = "Tipo de transacción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeButton(
                    text = "Gasto",
                    isSelected = uiState.type == TransactionType.EXPENSE,
                    onClick = { viewModel.onTypeChange(TransactionType.EXPENSE) },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    text = "Ingreso",
                    isSelected = uiState.type == TransactionType.INCOME,
                    onClick = { viewModel.onTypeChange(TransactionType.INCOME) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Amount Input
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChange(it) },
                label = { Text("Monto") },
                prefix = { Text("S/ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description Input
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Category Selector
            Text(
                text = "Categoría (opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            CategorySelector(
                categories = availableCategories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelected = { viewModel.onCategoryChange(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Error Message
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveTransaction {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Guardar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelector(
    categories: List<CategoryEntity>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (categories.isEmpty()) {
            Text(
                text = "No hay categorías disponibles. Crea algunas en Configuración.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // "Sin categoría" option
            CategoryChip(
                category = null,
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )

            // Category chips
            categories.forEach { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: CategoryEntity?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (category != null) {
                    Text(text = category.icon)
                    Text(text = category.name)
                } else {
                    Text(text = "Sin categoría")
                }
            }
        },
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun TypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
