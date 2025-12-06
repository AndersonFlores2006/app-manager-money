package com.example.gestor_money.presentation.screens.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val editingCategory by viewModel.editingCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddCategoryDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                categories.isEmpty() -> {
                    EmptyCategoriesView(onAddCategory = { viewModel.showAddCategoryDialog() })
                }
                else -> {
                    CategoriesList(
                        categories = categories,
                        onDeleteCategory = { viewModel.deleteCategory(it) },
                        onEditCategory = { viewModel.showEditCategoryDialog(it) }
                    )
                }
            }

            // Dialogs
            if (showAddDialog) {
                AddCategoryDialog(
                    onDismiss = { viewModel.hideAddCategoryDialog() },
                    onConfirm = { name, icon, color, type ->
                        viewModel.addCategory(name, icon, color, type)
                    }
                )
            }

            editingCategory?.let { category ->
                EditCategoryDialog(
                    category = category,
                    onDismiss = { viewModel.hideEditCategoryDialog() },
                    onConfirm = { name, icon, color, type ->
                        viewModel.updateCategory(category.id, name, icon, color, type)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyCategoriesView(onAddCategory: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No tienes categorías",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Crea categorías para organizar tus transacciones",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddCategory) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Crear primera categoría")
        }
    }
}

@Composable
private fun CategoriesList(
    categories: List<CategoryEntity>,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onEditCategory: (CategoryEntity) -> Unit
) {
    val incomeCategories = categories.filter { it.type == "INCOME" }
    val expenseCategories = categories.filter { it.type == "EXPENSE" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (incomeCategories.isNotEmpty()) {
            item {
                Text(
                    text = "Ingresos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(incomeCategories) { category ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -50 })
                ) {
                    CategoryItem(
                        category = category,
                        onEdit = { onEditCategory(category) },
                        onDelete = { onDeleteCategory(category) }
                    )
                }
            }
        }

        if (expenseCategories.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gastos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            items(expenseCategories) { category ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -50 })
                ) {
                    CategoryItem(
                        category = category,
                        onEdit = { onEditCategory(category) },
                        onDelete = { onDeleteCategory(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(category.color)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (category.type == "INCOME") "Ingreso" else "Gasto",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Add, // TODO: Use edit icon
                        contentDescription = "Editar"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("📁") }
    var selectedColor by remember { mutableStateOf(android.graphics.Color.BLUE) }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // TODO: Add icon picker and color picker
                Text("Icono: $selectedIcon")
                Text("Color: ${android.graphics.Color.valueOf(selectedColor)}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == TransactionType.INCOME,
                        onClick = { selectedType = TransactionType.INCOME },
                        label = { Text("Ingreso") }
                    )
                    FilterChip(
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = { selectedType = TransactionType.EXPENSE },
                        label = { Text("Gasto") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedIcon, selectedColor, selectedType.name)
                        onDismiss()
                    }
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EditCategoryDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }
    var selectedIcon by remember { mutableStateOf(category.icon) }
    var selectedColor by remember { mutableStateOf(category.color) }
    var selectedType by remember { mutableStateOf(if (category.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // TODO: Add icon picker and color picker
                Text("Icono: $selectedIcon")
                Text("Color: ${android.graphics.Color.valueOf(selectedColor)}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == TransactionType.INCOME,
                        onClick = { selectedType = TransactionType.INCOME },
                        label = { Text("Ingreso") }
                    )
                    FilterChip(
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = { selectedType = TransactionType.EXPENSE },
                        label = { Text("Gasto") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedIcon, selectedColor, selectedType.name)
                        onDismiss()
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}