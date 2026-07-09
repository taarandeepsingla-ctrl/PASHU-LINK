package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Message
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    sessionId: String,
    viewModel: PashuViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val messages by viewModel.getMessages(sessionId).collectAsState(initial = emptyList())
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var chatInput by remember { mutableStateOf("") }
    var activeTranslationLang by remember { mutableStateOf("Original") } // "Original", "English", "Hindi", "Punjabi"

    // Call / Video states
    var activeCallType by remember { mutableStateOf("") } // "", "Audio", "Video"
    var callSeconds by remember { mutableStateOf(0) }

    // Increment call timer
    LaunchedEffect(activeCallType) {
        if (activeCallType.isNotEmpty()) {
            callSeconds = 0
            while (activeCallType.isNotEmpty()) {
                delay(1000)
                callSeconds++
            }
        }
    }

    // Scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreen)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    val displayName = sessionId.replace("user_", "").replace("_", " ").replaceFirstChar { it.uppercase() }
                    Text(displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Online • Punjab Dairy Partner", fontSize = 11.sp, color = LightSage)
                }

                // Audio call
                IconButton(onClick = { activeCallType = "Audio" }) {
                    Icon(Icons.Default.Phone, contentDescription = "Audio Call", tint = Color.White)
                }
                // Video call
                IconButton(onClick = { activeCallType = "Video" }) {
                    Icon(Icons.Default.VideoCall, contentDescription = "Video Call", tint = Color.White)
                }
            }

            // Dynamic Language Translation Tool bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Auto-Translation Swapper:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // Toggle row
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                            .padding(2.dp)
                    ) {
                        listOf("Original", "English", "Hindi", "Punjabi").forEach { item ->
                            val label = when (item) {
                                "English" -> "EN"
                                "Hindi" -> "HI"
                                "Punjabi" -> "PB"
                                else -> "ORG"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (activeTranslationLang == item) MintGreen else Color.Transparent)
                                    .clickable { activeTranslationLang = item }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTranslationLang == item) Color.White else ForestGreen
                                )
                            }
                        }
                    }
                }
            }

            // Chat messages column
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isBuyer = msg.senderRole == "Buyer"
                    
                    // Decide text depending on translation language setting
                    val textToDisplay = when (activeTranslationLang) {
                        "English" -> msg.translationEn.ifEmpty { msg.text }
                        "Hindi" -> msg.translationHi.ifEmpty { msg.text }
                        "Punjabi" -> msg.translationPa.ifEmpty { msg.text }
                        else -> msg.text
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isBuyer) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isBuyer) 16.dp else 0.dp,
                                bottomEnd = if (isBuyer) 0.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isBuyer) ForestGreen else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth(0.82f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = textToDisplay,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isBuyer) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBuyer) "Sent" else "Seller",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBuyer) LightSage else MintGreen
                                    )
                                    if (activeTranslationLang != "Original") {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Translate, contentDescription = "Translated", tint = PaleLime, modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Fixed input bar with simulated attachments (voice note and image)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image attachment icon
                    IconButton(
                        onClick = {
                            chatInput = "[Shared Photo of Sahiwal Cow]"
                        }
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Attach Photo", tint = ForestGreen)
                    }

                    // Voice Note icon
                    IconButton(
                        onClick = {
                            chatInput = "[Simulated Voice Message - 0:12]"
                        }
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Message", tint = ForestGreen)
                    }

                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Write message in English, Hindi, PB...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                viewModel.sendChatMessage(sessionId, chatInput, "Buyer")
                                chatInput = ""
                            }
                        },
                        enabled = chatInput.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = ForestGreen)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }

        // 3. Simulated Active Calling Overlay Popup (Zero dead-ends!)
        AnimatedVisibility(
            visible = activeCallType.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activeCallType == "Video") Icons.Default.VideoCall else Icons.Default.Call,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Harpreet Singh",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Simulated $activeCallType Call via PashuLink",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PaleLime
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Duration: ${"%02d:%02d".format(callSeconds / 60, callSeconds % 60)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    // End call button
                    Button(
                        onClick = { activeCallType = "" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}
