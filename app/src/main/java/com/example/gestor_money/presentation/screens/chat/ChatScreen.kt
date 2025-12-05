package com.example.gestor_money.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gestor_money.presentation.screens.chat.viewmodel.ChatMessage
import com.example.gestor_money.presentation.screens.chat.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Asistente IA",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Investment advice button
                IconButton(
                    onClick = { viewModel.getInvestmentAdvice() },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Consejos de inversión",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Clear chat button
                IconButton(
                    onClick = { viewModel.clearChat() },
                    enabled = uiState.messages.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Limpiar chat",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.messages.isEmpty()) {
                item {
                    EmptyStateMessage()
                }
            } else {
                items(uiState.messages) { message ->
                    MessageBubble(message = message)
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                item {
                    LoadingIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input field
        MessageInputField(
            value = uiState.currentInput,
            onValueChange = { viewModel.updateInput(it) },
            onSend = { 
                viewModel.sendMessage(uiState.currentInput)
            },
            enabled = !uiState.isLoading
        )
    }
}

@Composable
private fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Hola! Soy tu asistente financiero",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pregúntame sobre tus finanzas o solicita consejos de inversión",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = when {
        message.isError -> MaterialTheme.colorScheme.errorContainer
        message.isUser -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = when {
        message.isError -> MaterialTheme.colorScheme.onErrorContainer
        message.isUser -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 100.dp),
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Escribe tu mensaje...") },
            enabled = enabled,
            maxLines = 4,
            shape = RoundedCornerShape(24.dp)
        )
        
        FilledIconButton(
            onClick = onSend,
            enabled = enabled && value.isNotBlank(),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar"
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
