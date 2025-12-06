package com.example.gestor_money.presentation.screens.categories

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

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                categoryRepository.getAllCategories().collect { categories ->
                    _categories.value = categories
                }
            } finally {
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

    fun addCategory(name: String, icon: String, color: Int, type: String) {
        viewModelScope.launch {
            try {
                val category = CategoryEntity(
                    name = name,
                    icon = icon,
                    color = color,
                    type = type
                )
                categoryRepository.insertCategory(category)
                hideAddCategoryDialog()
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun updateCategory(id: Long, name: String, icon: String, color: Int, type: String) {
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
                    hideEditCategoryDialog()
                }
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}