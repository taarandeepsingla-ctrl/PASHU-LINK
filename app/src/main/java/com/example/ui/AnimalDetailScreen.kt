package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Listing
import com.example.ui.components.MilkChart
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    listing: Listing,
    viewModel: PashuViewModel,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToTransport: () -> Unit,
    onNavigateToVet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val favorites by viewModel.favoritesList.collectAsState()
    val isFav = favorites.contains(listing.id)

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var showContactInfo by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // 1. Hero Image representation with back and favorite triggers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ForestGreen, MintGreen)
                        )
                    )
            ) {
                // Top controls overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(listing.id) },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) Color.Red else Color.White
                            )
                        }
                    }
                }

                // Header Breed Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(PaleLime, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = listing.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${listing.breed} ${listing.category.dropLast(1)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            // Content body
            Column(modifier = Modifier.padding(16.dp)) {

                // 2. Pricing & Distance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Listed Price", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = "₹${"%,.0f".format(listing.price)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = ForestGreen
                            )
                            if (listing.isNegotiable) {
                                Box(
                                    modifier = Modifier
                                        .background(LightSage, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Negotiable Price", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = MintGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${listing.distanceKm} km away", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("Listed in ${listing.location}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 15 Photos grid simulator
                Text(
                    text = "Livestock Media Gallery (15 Photos & Videos)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Loop 15 simulated photo thumbnails with different gradients
                    for (i in 1..15) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MintGreen.copy(alpha = 0.2f * (i % 4 + 1)),
                                            ForestGreen.copy(alpha = 0.1f * (i % 3 + 1))
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (i == 1) {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Video", tint = ForestGreen, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Pets, contentDescription = "Photo", tint = ForestGreen.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "#$i",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestGreen,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Animal Specs Grid
                Text(
                    text = "Animal Specifications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpecItemCard(title = "Age", value = "${listing.ageYears} Years", icon = Icons.Default.CalendarToday, modifier = Modifier.weight(1f))
                    SpecItemCard(title = "Weight", value = "${listing.weightKg} kg", icon = Icons.Default.Scale, modifier = Modifier.weight(1f))
                    SpecItemCard(title = "Pregnancy", value = listing.pregnancyStatus, icon = Icons.Default.ChildCare, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpecItemCard(title = "Lactation No.", value = "${listing.lactationNumber}", icon = Icons.Default.WaterDrop, modifier = Modifier.weight(1f))
                    SpecItemCard(title = "Breed Type", value = listing.breed, icon = Icons.Default.Pets, modifier = Modifier.weight(1f))
                    SpecItemCard(title = "Health Status", value = "Certified", icon = Icons.Default.VerifiedUser, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Milk Chart representation
                if (listing.dailyMilkYieldLiters > 0) {
                    MilkChart(averageYield = listing.dailyMilkYieldLiters)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 5. Digital Health Records & Vaccination
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MintGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Digital Health Record", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Vaccines Administered:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(listing.vaccinationHistory, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Government Health Certificate", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Text("ID: ${listing.healthCertificateUrl}", style = MaterialTheme.typography.bodySmall, color = MintGreen)
                            }

                            Box(
                                modifier = Modifier
                                    .background(PaleLime.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VERIFIED FIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Owner info block
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(listing.ownerName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(listing.ownerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified Seller", tint = MintGreen, modifier = Modifier.size(16.dp))
                            }
                            Text("Punjab Dairy Union Member", fontSize = 11.sp, color = Color.Gray)
                        }

                        Button(
                            onClick = { showContactInfo = !showContactInfo },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                        ) {
                            Text(if (showContactInfo) "Hide Phone" else "Call Owner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (showContactInfo) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightSage.copy(alpha = 0.5f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Contact Number: ${listing.ownerPhone}",
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Bottom Fixed Quick Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chat button
                Button(
                    onClick = {
                        val session = "user_${listing.ownerName.replace(" ", "_").lowercase()}"
                        viewModel.sendChatMessage(session, "Hello ${listing.ownerName}, I am interested in your ${listing.breed} listed in ${listing.location}.", "Buyer")
                        onNavigateToChat(session)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat with Seller", fontWeight = FontWeight.Bold)
                }

                // Transport Booking
                IconButton(
                    onClick = onNavigateToTransport,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MintGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = "Book Transport", tint = ForestGreen)
                }

                // Veterinarian booking
                IconButton(
                    onClick = onNavigateToVet,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MintGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = "Book Vet Checkup", tint = ForestGreen)
                }
            }
        }
    }
}

@Composable
fun SpecItemCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
        }
    }
}
