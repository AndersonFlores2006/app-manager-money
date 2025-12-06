package com.example.gestor_money.presentation.screens.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _editingCategory = MutableStateFlow<CategoryEntity?>(null)
    val editingCategory: StateFlow<CategoryEntity?> = _editingCategory.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var _defaultCategoriesCreated = false

    init {
        Log.d("CategoriesViewModel", "Initializing CategoriesViewModel")
        loadCategories()
    }

    private fun loadCategories() {
        Log.d("CategoriesViewModel", "Loading categories...")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                categoryRepository.getAllCategories().collect { categories ->
                    Log.d("CategoriesViewModel", "Categories loaded: ${categories.size}")
                    _categories.value = categories
                    _isLoading.value = false

                    // If no categories, create default ones (only once)
                    if (categories.isEmpty() && !_defaultCategoriesCreated) {
                        _defaultCategoriesCreated = true
                        Log.d("CategoriesViewModel", "No categories found, creating default categories")
                        categoryRepository.createDefaultCategories()
                        // The flow will automatically emit the new categories
                    }
                }
            } catch (e: Exception) {
                Log.e("CategoriesViewModel", "Error loading categories", e)
                _isLoading.value = false
            }
        }
    }

    fun showAddCategoryDialog() {
        _showAddDialog.value = true
    }

    fun hideAddCategoryDialog() {
        _showAddDialog.value = false
    }

    fun showEditCategoryDialog(category: CategoryEntity) {
        _editingCategory.value = category
    }

    fun hideEditCategoryDialog() {
        _editingCategory.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun addCategory(name: String, icon: String, color: Int, type: String) {
        Log.d("CategoriesViewModel", "Adding category: $name, type: $type")
        
        // Validaciones
        if (name.isBlank()) {
            _error.value = "El nombre de la categoría es requerido"
            return
        }
        
        if (icon.isBlank()) {
            _error.value = "Debes seleccionar un icono"
            return
        }
        
        viewModelScope.launch {
            try {
                _error.value = null // Limpiar errores previos
                
                val category = CategoryEntity(
                    name = name.trim(),
                    icon = icon,
                    color = color,
                    type = type,
                    lastModified = System.currentTimeMillis()
                )
                
                val id = categoryRepository.insertCategory(category)
                Log.d("CategoriesViewModel", "Category added with id: $id")
                hideAddCategoryDialog()
                _error.value = null // Limpiar errores en caso de éxito
                
            } catch (e: Exception) {
                Log.e("CategoriesViewModel", "Error adding category", e)
                _error.value = "Error al crear la categoría: ${e.message}"
            }
        }
    }

    fun updateCategory(id: Long, name: String, icon: String, color: Int, type: String) {
        Log.d("CategoriesViewModel", "Updating category id: $id, name: $name")
        viewModelScope.launch {
            try {
                val existingCategory = _categories.value.find { it.id == id }
                if (existingCategory != null) {
                    val updatedCategory = existingCategory.copy(
                        name = name,
                        icon = icon,
                        color = color,
                        type = type
                    )
                    categoryRepository.updateCategory(updatedCategory)
                    Log.d("CategoriesViewModel", "Category updated")
                    hideEditCategoryDialog()
                } else {
                    Log.w("CategoriesViewModel", "Category with id $id not found")
                }
            } catch (e: Exception) {
                Log.e("CategoriesViewModel", "Error updating category", e)
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        Log.d("CategoriesViewModel", "Deleting category: ${category.name} (id: ${category.id})")
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
                Log.d("CategoriesViewModel", "Category deleted")
            } catch (e: Exception) {
                Log.e("CategoriesViewModel", "Error deleting category", e)
            }
        }
    }
}