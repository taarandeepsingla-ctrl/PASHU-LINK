package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import androidx.compose.foundation.border
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime

data class StoreItem(
    val id: String,
    val title: String,
    val price: Double,
    val category: String, // Feed, Equipment
    val sub: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DairyStoreScreen(
    viewModel: PashuViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    var activeSubTab by remember { mutableStateOf(0) } // 0: Feed, 1: Equipment, 2: FinTech & Breeding

    val storeItems = listOf(
        StoreItem("1", "Verka Premium Cattle Feed (50kg)", 1450.0, "Feed", "Balances lactation & digestion"),
        StoreItem("2", "Chelated Mineral Mixture (5kg)", 950.0, "Feed", "Boosts reproductive health & yield"),
        StoreItem("3", "Bypass Fat High Energy (1kg)", 350.0, "Feed", "Enhances milk fat percentage"),
        StoreItem("4", "Organic Berseem Clover Seed (10kg)", 1200.0, "Feed", "Nutrient-rich daily green fodder"),
        StoreItem("5", "Automatic Double Bucket Milking Machine", 24000.0, "Equipment", "Heavy duty single-phase motor"),
        StoreItem("6", "Veterinary Standard Cow Rubber Mat", 1850.0, "Equipment", "Improves hoof health & comfort"),
        StoreItem("7", "Stainless Steel Milch Can (20 Liters)", 2200.0, "Equipment", "AISI 304 food-grade steel"),
        StoreItem("8", "Electric Fodder & Silage Cutter", 15500.0, "Equipment", "2 HP single-phase copper motor")
    )

    val activeItems = when (activeSubTab) {
        0 -> storeItems.filter { it.category == "Feed" }
        1 -> storeItems.filter { it.category == "Equipment" }
        else -> emptyList()
    }

    var selectedItemForBuy by remember { mutableStateOf<StoreItem?>(null) }
    var loanApplied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
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
                Text("PashuLink Agribusiness Store", style = MaterialTheme.typography.titleSmall, color = LightSage, fontWeight = FontWeight.Bold)
                Text("Cattle Feed, Equipment & Farm Loans", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }

        // Sub Tabs
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }, text = { Text("Feed & Minerals", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }, text = { Text("Dairy Equipment", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }, text = { Text("Loans & Insemination", fontWeight = FontWeight.Bold) })
        }

        if (activeSubTab == 0 || activeSubTab == 1) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(activeItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BentoBorderColor, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(84.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LightSage),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.category == "Feed") Icons.Default.Grass else Icons.Default.Hardware,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                item.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                maxLines = 1,
                                color = ForestGreen
                            )
                            Text(
                                item.sub,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                minLines = 2,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 13.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "₹${"%,.0f".format(item.price)}",
                                    fontWeight = FontWeight.Black,
                                    color = ForestGreen,
                                    fontSize = 14.sp
                                )
                                
                                Button(
                                    onClick = { selectedItemForBuy = item },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Buy", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Loans & Insemination
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                // NABARD Dairy loan Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoBorderColor, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = PaleLime, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("NABARD Interest-Subsidized Dairy Loan", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = ForestGreen)
                        }

                        Text(
                            text = "Apply for central cattle purchasing loans under DEDS (Dairy Entrepreneurship Development Scheme) with up to 33% capital subsidy and simple 4% interest.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        if (!loanApplied) {
                            Button(
                                onClick = { loanApplied = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Check Loan Eligibility & Apply Now", fontWeight = FontWeight.Black)
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = LightSage)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Application Submitted! Ludhiana Co-op Bank is reviewing your records.", fontSize = 12.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breeding AI Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoBorderColor, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = ForestGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Artificial Insemination (AI) Booking", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = ForestGreen)
                        }

                        Text(
                            text = "Order high-quality pedigree semen straws (Sahiwal, Jersey, Murrah) from certified Government breeding banks.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Book Artificial Insemination (₹350)", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Simulated Checkout Modal Dialog
        AnimatedVisibility(
            visible = selectedItemForBuy != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                selectedItemForBuy?.let { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Escrow Purchase Checkout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = ForestGreen)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, textAlign = TextAlign.Center)
                            Text(item.sub, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Item Cost:", fontSize = 13.sp)
                                Text("₹${item.price}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Platform Escrow Charge:", fontSize = 13.sp)
                                Text("₹0 (Promotion)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MintGreen)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${item.price}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = ForestGreen)
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { selectedItemForBuy = null },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Pay securely with UPI / Wallet", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { selectedItemForBuy = null }) {
                                Text("Cancel", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
