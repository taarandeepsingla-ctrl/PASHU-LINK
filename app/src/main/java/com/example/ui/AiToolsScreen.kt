package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsScreen(
    viewModel: PashuViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    var activeTool by remember { mutableStateOf(0) } // 0: Price, 1: Fraud, 2: Feed, 3: Chat Guru

    val aiResponse by viewModel.aiResponseText.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    // Inputs for tools
    var breedInput by remember { mutableStateOf("Sahiwal") }
    var ageInput by remember { mutableStateOf("3") }
    var weightInput by remember { mutableStateOf("420") }
    var yieldInput by remember { mutableStateOf("15") }
    var priceInput by remember { mutableStateOf("75000") }
    var locationInput by remember { mutableStateOf("Ludhiana, Punjab") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Core Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ForestGreen, ForestGreen.copy(alpha = 0.9f))
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PaleLime, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gemini AI Agricultural Assist",
                        style = MaterialTheme.typography.titleSmall,
                        color = LightSage,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "PashuLink Smart AI Tools",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        // Horizontal tools tab selector
        ScrollableTabRow(
            selectedTabIndex = activeTool,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTool]),
                    color = MintGreen
                )
            }
        ) {
            Tab(selected = activeTool == 0, onClick = { activeTool = 0; viewModel.aiResponseText.value = "" }, text = { Text("Price Estimator", fontWeight = FontWeight.Bold) })
            Tab(selected = activeTool == 1, onClick = { activeTool = 1; viewModel.aiResponseText.value = "" }, text = { Text("Fraud Auditor", fontWeight = FontWeight.Bold) })
            Tab(selected = activeTool == 2, onClick = { activeTool = 2; viewModel.aiResponseText.value = "" }, text = { Text("Feed Planner", fontWeight = FontWeight.Bold) })
            Tab(selected = activeTool == 3, onClick = { activeTool = 3 }, text = { Text("AI Dairy Guru", fontWeight = FontWeight.Bold) })
        }

        if (activeTool != 3) {
            // Standard scrollable form page for calculation tools
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = when (activeTool) {
                                    0 -> "Calculate Fair Livestock Value"
                                    1 -> "Audit Listing for Fraud Indicators"
                                    else -> "Generate Custom Feed Recipe"
                                },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Fields based on selection
                            OutlinedTextField(
                                value = breedInput,
                                onValueChange = { breedInput = it },
                                label = { Text("Livestock Breed") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (activeTool == 0 || activeTool == 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = ageInput,
                                        onValueChange = { ageInput = it },
                                        label = { Text("Age (Years)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = weightInput,
                                        onValueChange = { weightInput = it },
                                        label = { Text("Weight (kg)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = yieldInput,
                                    onValueChange = { yieldInput = it },
                                    label = { Text("Daily Milk Yield (L)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                if (activeTool == 1) {
                                    OutlinedTextField(
                                        value = priceInput,
                                        onValueChange = { priceInput = it },
                                        label = { Text("Price (INR)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                } else if (activeTool == 0) {
                                    OutlinedTextField(
                                        value = locationInput,
                                        onValueChange = { locationInput = it },
                                        label = { Text("Location") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    when (activeTool) {
                                        0 -> viewModel.runFairValueCalculator(
                                            "Cows", breedInput, ageInput.toDoubleOrNull() ?: 3.0,
                                            weightInput.toIntOrNull() ?: 400, yieldInput.toDoubleOrNull() ?: 12.0, locationInput
                                        )
                                        1 -> viewModel.runFraudRiskDetector(
                                            breedInput, ageInput.toDoubleOrNull() ?: 3.0,
                                            yieldInput.toDoubleOrNull() ?: 12.0, priceInput.toDoubleOrNull() ?: 60000.0, "Punjab"
                                        )
                                        2 -> viewModel.runFeedRecommender(breedInput, "Cows", yieldInput.toDoubleOrNull() ?: 12.0)
                                    }
                                },
                                enabled = !aiLoading && breedInput.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (aiLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Ask Gemini AI Assistant", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // AI Output Display Card
                if (aiLoading || aiResponse.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LightSage.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Recommend, contentDescription = null, tint = ForestGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Gemini AI Report Output", fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                if (aiLoading && aiResponse.isEmpty()) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        LinearProgressIndicator(color = MintGreen, modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Querying Gemini models...", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                } else {
                                    Text(
                                        text = aiResponse,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Interactive AI Dairy Guru Chat Page
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val guruMessages by viewModel.aiGuruMessages.collectAsState()
                val guruLoading by viewModel.aiGuruLoading.collectAsState()
                var chatInput by remember { mutableStateOf("") }
                val lazyListState = rememberLazyListState()

                // Auto-scroll on new messages
                LaunchedEffect(guruMessages.size) {
                    if (guruMessages.isNotEmpty()) {
                        lazyListState.animateScrollToItem(guruMessages.size - 1)
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(guruMessages) { msg ->
                        val isUser = msg.senderRole == "Buyer"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 0.dp,
                                    bottomEnd = if (isUser) 0.dp else 16.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUser) ForestGreen else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isUser) "Farmer" else "AI Dairy Guru",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = if (isUser) PaleLime else MintGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    if (guruLoading) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.5f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = MintGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guru is typing...", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Chat Input bar
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
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Ask about vaccines, subsidy, silage...") },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    viewModel.askAiGuru(chatInput)
                                    chatInput = ""
                                }
                            },
                            enabled = !guruLoading && chatInput.isNotBlank(),
                            colors = IconButtonDefaults.iconButtonColors(containerColor = ForestGreen)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
