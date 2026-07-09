package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime

data class MapPin(
    val id: String,
    val title: String,
    val sub: String,
    val type: String, // "Livestock", "Clinic", "Truck"
    val xOffset: Float,
    val yOffset: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: PashuViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    var selectedPin by remember { mutableStateOf<MapPin?>(null) }
    var activeFilter by remember { mutableStateOf("All") } // "All", "Livestock", "Clinic", "Truck"

    val mapPins = listOf(
        MapPin("1", "Sahiwal Cow (Harpreet's Farm)", "₹72,000 • 2.4 km away", "Livestock", 120f, 150f),
        MapPin("2", "Murrah Buffalo (Gurdev's Hub)", "₹1,15,000 • 5.1 km away", "Livestock", 320f, 110f),
        MapPin("3", "Ludhiana Veterinary Clinic", "Dr. Amanpreet • 1.5 km away", "Clinic", 200f, 250f),
        MapPin("4", "Sher-E-Punjab Truck Carrier", "Ludhiana Stand • 3.2 km away", "Truck", 400f, 320f),
        MapPin("5", "Nili Ravi (Sukhbir's Dairy)", "₹98,000 • 4.8 km away", "Livestock", 240f, 380f)
    )

    val filteredPins = if (activeFilter == "All") mapPins else mapPins.filter { it.type == activeFilter }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ForestGreen)
                .padding(16.dp)
        ) {
            Column {
                Text("PashuLink Map Locator", style = MaterialTheme.typography.titleSmall, color = LightSage, fontWeight = FontWeight.Bold)
                Text("Locate Animals & Services Nearby", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }

        // Horizontal filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Livestock", "Clinic", "Truck").forEach { filter ->
                ElevatedFilterChip(
                    selected = activeFilter == filter,
                    onClick = {
                        activeFilter = filter
                        selectedPin = null
                    },
                    label = { Text(filter) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Custom Vector Map Canvas representing Punjab grid roads
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(LightSage.copy(alpha = 0.2f))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color.LightGray.copy(alpha = 0.4f)
                val roadColor = Color.White
                
                // Draw decorative roads
                drawLine(roadColor, Offset(0f, 200f), Offset(size.width, 240f), strokeWidth = 14.dp.toPx())
                drawLine(roadColor, Offset(200f, 0f), Offset(250f, size.height), strokeWidth = 14.dp.toPx())
                drawLine(roadColor, Offset(0f, 400f), Offset(size.width, 380f), strokeWidth = 10.dp.toPx())

                // Draw circles represent city centres
                drawCircle(Brush.radialGradient(listOf(PaleLime.copy(alpha = 0.3f), Color.Transparent)), radius = 120.dp.toPx(), center = Offset(200f, 220f))
            }

            // Render interactive Pins on coordinate offsets
            filteredPins.forEach { pin ->
                val pinColor = when (pin.type) {
                    "Livestock" -> ForestGreen
                    "Clinic" -> Color.Red
                    else -> MintGreen
                }

                val pinIcon = when (pin.type) {
                    "Livestock" -> Icons.Default.Pets
                    "Clinic" -> Icons.Default.MedicalServices
                    else -> Icons.Default.LocalShipping
                }

                Box(
                    modifier = Modifier
                        .offset(x = pin.xOffset.dp, y = pin.yOffset.dp)
                        .clip(CircleShape)
                        .background(pinColor)
                        .clickable { selectedPin = pin }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(pinIcon, contentDescription = pin.title, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            // 3. Info overlay card for selected Node
            if (selectedPin != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    val pin = selectedPin!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val pinIcon = when (pin.type) {
                                "Livestock" -> Icons.Default.Pets
                                "Clinic" -> Icons.Default.MedicalServices
                                else -> Icons.Default.LocalShipping
                            }
                            val cardBg = when (pin.type) {
                                "Livestock" -> ForestGreen
                                "Clinic" -> Color.Red
                                else -> MintGreen
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(cardBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(pinIcon, contentDescription = null, tint = Color.White)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(pin.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(pin.sub, fontSize = 12.sp, color = Color.Gray)
                            }

                            IconButton(
                                onClick = { selectedPin = null },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
