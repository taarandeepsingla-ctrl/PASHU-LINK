package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.*
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
import com.example.data.Listing
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(
    viewModel: PashuViewModel,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val allListings by viewModel.listingsList.collectAsState()

    var showAddForm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Form inputs
    var category by remember { mutableStateOf("Cows") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var yield by remember { mutableStateOf("") }
    var lactation by remember { mutableStateOf("") }
    var pregnancy by remember { mutableStateOf("Not Pregnant") }
    var vaccination by remember { mutableStateOf("Fully Vaccinated") }
    var location by remember { mutableStateOf("Ludhiana, Punjab") }
    var price by remember { mutableStateOf("") }
    var isNegotiable by remember { mutableStateOf(true) }

    // Analytics computation
    val myName = viewModel.currentUserName.collectAsState().value
    val myListings = allListings.filter { it.ownerName == myName }
    val activeCount = myListings.count { !it.isSold }
    val soldCount = myListings.count { it.isSold }
    val totalEarnings = myListings.filter { it.isSold }.sumOf { it.price }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Analytics & Earnings Banner
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
                        text = "Seller Command Dashboard",
                        style = MaterialTheme.typography.titleSmall,
                        color = LightSage,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Welcome back, $myName",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row of stats cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox(title = "My Animals", value = "${myListings.size}", icon = Icons.Default.Pets, modifier = Modifier.weight(1f))
                        StatBox(title = "Sold Out", value = "$soldCount", icon = Icons.Default.CheckCircle, modifier = Modifier.weight(1f))
                        StatBox(title = "Earnings", value = "₹${"%,.0f".format(totalEarnings)}", icon = Icons.Default.CurrencyRupee, modifier = Modifier.weight(1.2f))
                    }
                }
            }
        }

        // 2. Add New Livestock Collapsible Form
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
                            .clickable { showAddForm = !showAddForm },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = MintGreen, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "List a New Animal for Sale",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen
                            )
                        }

                        Icon(
                            imageVector = if (showAddForm) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = ForestGreen
                        )
                    }

                    AnimatedVisibility(visible = showAddForm) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Category selector
                            Text("Select Category:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            var expandedCat by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { expandedCat = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(category, fontWeight = FontWeight.Bold, color = ForestGreen)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ForestGreen)
                                    }
                                }
                                DropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                                    listOf("Cows", "Buffaloes", "Bulls", "Calves", "Goats", "Sheep", "Horses").forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                category = cat
                                                expandedCat = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Breed
                            OutlinedTextField(
                                value = breed,
                                onValueChange = { breed = it },
                                label = { Text("Breed (e.g. Sahiwal, Murrah)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = age,
                                    onValueChange = { age = it },
                                    label = { Text("Age (Years)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { weight = it },
                                    label = { Text("Weight (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = yield,
                                    onValueChange = { yield = it },
                                    label = { Text("Daily Milk Yield (L)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = lactation,
                                    onValueChange = { lactation = it },
                                    label = { Text("Lactation No.") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = pregnancy,
                                    onValueChange = { pregnancy = it },
                                    label = { Text("Pregnancy (Not / 3m / 5m)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = location,
                                    onValueChange = { location = it },
                                    label = { Text("Location (District, PB)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Price (INR)") },
                                prefix = { Text("₹ ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PaleLime.copy(alpha = 0.15f))
                                    .clickable { isNegotiable = !isNegotiable }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = isNegotiable, onCheckedChange = { isNegotiable = it })
                                Text("Price is Negotiable (Recommended)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestGreen)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (breed.isNotEmpty() && price.isNotEmpty()) {
                                        viewModel.addListing(
                                            category = category,
                                            breed = breed,
                                            age = age.toDoubleOrNull() ?: 3.0,
                                            weight = weight.toIntOrNull() ?: 400,
                                            milkYield = yield.toDoubleOrNull() ?: 12.0,
                                            lactation = lactation.toIntOrNull() ?: 2,
                                            pregnancy = pregnancy,
                                            vaccination = vaccination,
                                            location = location,
                                            price = price.toDoubleOrNull() ?: 50000.0,
                                            isNegotiable = isNegotiable,
                                            photosCsv = "default_animal"
                                        )
                                        // Reset
                                        breed = ""
                                        age = ""
                                        weight = ""
                                        yield = ""
                                        lactation = ""
                                        price = ""
                                        showAddForm = false
                                    }
                                },
                                enabled = breed.isNotEmpty() && price.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                            ) {
                                Text("Publish Livestock Listing", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. My Listings header
        item {
            Text(
                text = "My Livestock Listings (" + myListings.size + ")",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = ForestGreen
            )
        }

        // 4. Sellers listed items
        if (myListings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Pets, contentDescription = null, tint = MintGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("You haven't listed any livestock yet.", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Tap 'List a New Animal' above to start earning.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(myListings) { item ->
                SellerListingCard(
                    listing = item,
                    onMarkSold = { viewModel.markAsSold(item.id) },
                    onDelete = { viewModel.deleteListing(item) },
                    onCardClick = { onListingClick(item) }
                )
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(14.dp)
    ) {
        Column {
            Icon(icon, contentDescription = title, tint = PaleLime, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 10.sp, color = LightSage, maxLines = 1, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White, maxLines = 1)
        }
    }
}

@Composable
fun SellerListingCard(
    listing: Listing,
    onMarkSold: () -> Unit,
    onDelete: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick() }
            .border(1.dp, BentoBorderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightSage),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Pets, contentDescription = null, tint = ForestGreen)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${listing.breed} ${listing.category.dropLast(1)}",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ForestGreen
                    )
                    Text("Listed in: ${listing.location}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${"%,.0f".format(listing.price)}",
                        fontWeight = FontWeight.Black,
                        color = ForestGreen,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(if (listing.isSold) Color.Red.copy(alpha = 0.12f) else LightSage, RoundedCornerShape(6.dp))
                            .border(1.dp, if (listing.isSold) Color.Red.copy(alpha = 0.3f) else ForestGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (listing.isSold) "SOLD OUT" else "ACTIVE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = if (listing.isSold) Color.Red else ForestGreen
                        )
                    }
                }
            }

            if (!listing.isSold) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BentoBorderColor)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete listing
                    TextButton(onClick = onDelete) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row {
                        // Promote listing
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.padding(end = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BentoBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = MintGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Promote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Mark as Sold trigger
                        Button(
                            onClick = onMarkSold,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Sold", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
