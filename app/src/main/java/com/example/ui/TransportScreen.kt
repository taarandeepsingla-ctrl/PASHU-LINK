package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Booking
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
    viewModel: PashuViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val bookings by viewModel.bookingsList.collectAsState()
    val transportBookings = bookings.filter { it.type == "Transport" }

    var showBookForm by remember { mutableStateOf(false) }
    var pickupLocation by remember { mutableStateOf("Ludhiana Farm") }
    var deliveryLocation by remember { mutableStateOf("Amritsar Dairy Hub") }
    var selectedTruckType by remember { mutableStateOf("Large Animal Container Truck") }
    var dateVal by remember { mutableStateOf("2026-07-12") }

    var activeTrackingProgress by remember { mutableStateOf(0.3f) }

    // Increment progress to show live route tracking
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (activeTrackingProgress < 1.0f) {
                activeTrackingProgress += 0.05f
            } else {
                activeTrackingProgress = 0.1f
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Header
        item {
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
                    Text("PashuLink logistics network", style = MaterialTheme.typography.titleSmall, color = LightSage, fontWeight = FontWeight.Bold)
                    Text("Livestock Transport & Truck Booking", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }

        // 2. Booking form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBookForm = !showBookForm },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MintGreen, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Book Fodder or Live Animal Carriers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                        Icon(imageVector = if (showBookForm) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = ForestGreen)
                    }

                    AnimatedVisibility(visible = showBookForm) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            OutlinedTextField(
                                value = pickupLocation,
                                onValueChange = { pickupLocation = it },
                                label = { Text("Pickup Address (Farm)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = deliveryLocation,
                                onValueChange = { deliveryLocation = it },
                                label = { Text("Delivery Destination (Buyer's Farm/Dairy)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Carrier Size Required:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Large Container Truck", "Chota Hathi (Small)", "Open Trailer").forEach { type ->
                                    ElevatedFilterChip(
                                        selected = selectedTruckType.contains(type.take(6)),
                                        onClick = { selectedTruckType = type },
                                        label = { Text(type) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = dateVal,
                                onValueChange = { dateVal = it },
                                label = { Text("Preferred Journey Date") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (pickupLocation.isNotEmpty() && deliveryLocation.isNotEmpty()) {
                                        viewModel.bookVetOrTransport(
                                            type = "Transport",
                                            provider = "Sher-E-Punjab Logistics Ltd",
                                            date = dateVal,
                                            slot = "08:00 AM Departure",
                                            details = "Transport $selectedTruckType from $pickupLocation to $deliveryLocation",
                                            price = 4500.0
                                        )
                                        showBookForm = false
                                    }
                                },
                                enabled = pickupLocation.isNotEmpty() && deliveryLocation.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Calculate Freight & Confirm - Est. ₹4500", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Live Active tracking tracker
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Live Active Delivery Telemetry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Live Transit Tracking", style = MaterialTheme.typography.labelSmall, color = PaleLime, fontWeight = FontWeight.Bold)
                                Text("Ludhiana to Jalandhar (Carrier SB-883)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("ETA: 35 min", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress line representation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                            LinearProgressIndicator(
                                progress = { activeTrackingProgress },
                                color = PaleLime,
                                trackColor = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PaleLime)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Driver: Gurinder Dhillon (★4.9 Rating)", fontSize = 11.sp, color = LightSage, fontWeight = FontWeight.Bold)
                            Text("Secure OTP: 7721", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Bookings list
        item {
            Text(
                text = "My Transport Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = ForestGreen
            )
        }

        if (transportBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MintGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No active truck freight bookings yet.", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(transportBookings) { booking ->
                BookingVetCard(booking = booking, onRate = { rating -> viewModel.rateBooking(booking.id, rating) })
            }
        }
    }
}
