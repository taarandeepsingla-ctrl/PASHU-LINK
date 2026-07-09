package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Report
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
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: PashuViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isUserVerified by viewModel.isUserVerified.collectAsState()

    var mockPendingUsers by remember { mutableStateOf(listOf(
        "Sardar Gurcharan Singh (Bathinda Seller)",
        "Amandeep Kaur (Ludhiana Vet Clinic)",
        "Major Singh Johal (Amritsar Dairy Farm)"
    )) }

    var mockReports by remember { mutableStateOf(listOf(
        "Fake Sahiwal listing listed at ₹10,000 (Reported by 4 users)",
        "Unregistered broker posing as certified vet (Reported by 2 users)"
    )) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Core Header
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
                    Text(
                        text = "PashuLink Punjab Admin System",
                        style = MaterialTheme.typography.titleSmall,
                        color = LightSage,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Escrow & Platform Security Panel",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox(title = "Escrow Balance", value = "₹2,45,000", icon = Icons.Default.CurrencyRupee, modifier = Modifier.weight(1.1f))
                        StatBox(title = "Fake Reports", value = "${mockReports.size} Open", icon = Icons.Default.Warning, modifier = Modifier.weight(0.9f))
                        StatBox(title = "Subs Earnings", value = "₹42,500", icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 2. Identity Verification requests
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Aadhaar Identity Verification Portal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (mockPendingUsers.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("All user verification queues are clear!", fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                    }
                } else {
                    mockPendingUsers.forEach { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(user, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Aadhaar ID doc attached (AI score: 98%)", fontSize = 10.sp, color = Color.Gray)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { mockPendingUsers = mockPendingUsers.filter { it != user } },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Red.copy(alpha = 0.15f))
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Red)
                                    }

                                    Button(
                                        onClick = {
                                            mockPendingUsers = mockPendingUsers.filter { it != user }
                                            if (user.contains("Sardar")) {
                                                viewModel.isUserVerified.value = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Approve", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Fraud and Fake listings reports
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Flagged Fake Listings Moderation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (mockReports.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Text("No listings reported for fraud.", fontWeight = FontWeight.Bold, color = MintGreen)
                        }
                    }
                } else {
                    mockReports.forEach { r ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(r, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { mockReports = mockReports.filter { it != r } }) {
                                        Text("Dismiss", color = Color.Gray, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { mockReports = mockReports.filter { it != r } },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text("Remove Listing", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Platform revenue details
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Platform Revenue Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RevenueRow(label = "Featured Animal Listings fee", value = "₹250 per month")
                        RevenueRow(label = "Premium Seller Subscriptions", value = "₹1,200 annually")
                        RevenueRow(label = "Certified Vet Commission", value = "10% per booking")
                        RevenueRow(label = "Fodder Marketplace Commission", value = "3.5% per cart transaction")
                        RevenueRow(label = "Escrow Transaction Commission", value = "1.5% of final sell price")
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Text(value, fontWeight = FontWeight.ExtraBold, color = ForestGreen, fontSize = 12.sp)
    }
}
