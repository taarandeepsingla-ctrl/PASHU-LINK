package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.example.data.Listing
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PashuViewModel,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val activeCategory by viewModel.filterCategory.collectAsState()
    val activeBreed by viewModel.filterBreed.collectAsState()
    val maxPrice by viewModel.filterMaxPrice.collectAsState()
    val minMilk by viewModel.filterMinMilk.collectAsState()
    val onlyVerified by viewModel.filterOnlyVerified.collectAsState()
    val onlyPregnant by viewModel.filterOnlyPregnant.collectAsState()

    val listings by viewModel.filteredListings.collectAsState()
    val featuredListings by viewModel.featuredListings.collectAsState()

    var showFilters by remember { mutableStateOf(false) }

    val categories = listOf("All", "Cows", "Buffaloes", "Bulls", "Calves", "Goats", "Sheep", "Horses")
    val breeds = listOf("All", "Sahiwal", "Murrah", "Nili Ravi", "Gir", "Beetal", "Marwari")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Welcome & Language Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "PL",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                "PashuLink",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = ForestGreen,
                                letterSpacing = (-0.5).sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "📍 Ludhiana, Punjab",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Language Toggle Row
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.dp, BentoBorderColor, RoundedCornerShape(20.dp))
                            .padding(3.dp)
                    ) {
                        listOf("EN", "HI", "PB").forEach { code ->
                            val label = when (code) {
                                "EN" -> "English"
                                "HI" -> "Hindi"
                                else -> "Punjabi"
                            }
                            val active = lang == label
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (active) ForestGreen else Color.Transparent)
                                    .clickable { viewModel.currentLanguage.value = label }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (active) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Search Bar & Advanced Filter Toggle (Outer Item)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text(LanguageHelper.get("search_hint", lang), color = Color.Gray.copy(alpha = 0.8f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForestGreen) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BentoBorderColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (showFilters) LightSage else Color.White)
                        .clickable { showFilters = !showFilters }
                        .border(1.dp, if (showFilters) ForestGreen else BentoBorderColor, RoundedCornerShape(20.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Advanced Filters",
                        tint = if (showFilters) ForestGreen else Color.Gray
                    )
                }
            }
        }

        // 3. Expandable Advanced Filters Sheet
        item {
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Smart Marketplace Filters", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ForestGreen)
                            TextButton(onClick = {
                                viewModel.filterBreed.value = "All"
                                viewModel.filterMaxPrice.value = 350000.0
                                viewModel.filterMinMilk.value = 0.0
                                viewModel.filterOnlyVerified.value = false
                                viewModel.filterOnlyPregnant.value = false
                            }) {
                                Text("Reset All", fontSize = 12.sp, color = MintGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Breed Selector
                        Text("Select Breed:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(breeds) { b ->
                                FilterChip(
                                    selected = activeBreed == b,
                                    onClick = { viewModel.filterBreed.value = b },
                                    label = { Text(b) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PaleLime,
                                        selectedLabelColor = ForestGreen
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price range slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Price: ₹${maxPrice.toInt()}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = maxPrice.toFloat(),
                            onValueChange = { viewModel.filterMaxPrice.value = it.toDouble() },
                            valueRange = 10000f..35000f * 10f,
                            colors = SliderDefaults.colors(thumbColor = MintGreen, activeTrackColor = ForestGreen)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Min Milk yield slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min Daily Milk: ${"%.1f".format(minMilk)} Liters", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = minMilk.toFloat(),
                            onValueChange = { viewModel.filterMinMilk.value = it.toDouble() },
                            valueRange = 0f..30f,
                            colors = SliderDefaults.colors(thumbColor = MintGreen, activeTrackColor = ForestGreen)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Switched/Boolean checkmarks
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (onlyVerified) PaleLime.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { viewModel.filterOnlyVerified.value = !onlyVerified }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = onlyVerified, onCheckedChange = { viewModel.filterOnlyVerified.value = it })
                                Text("Nearby (<15km)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (onlyPregnant) PaleLime.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { viewModel.filterOnlyPregnant.value = !onlyPregnant }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = onlyPregnant, onCheckedChange = { viewModel.filterOnlyPregnant.value = it })
                                Text("Pregnant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 4. Horizontal Categories Row
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = LanguageHelper.get("categories", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        val selected = activeCategory == cat
                        val icon = when (cat) {
                            "All" -> Icons.Default.GridOn
                            "Cows" -> Icons.Default.Pets
                            "Buffaloes" -> Icons.Default.WaterDrop
                            "Bulls" -> Icons.Default.Shield
                            "Calves" -> Icons.Default.ChildCare
                            "Goats" -> Icons.Default.Grass
                            "Sheep" -> Icons.Default.Cloud
                            else -> Icons.Default.Star
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) ForestGreen else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.filterCategory.value = cat }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = cat, tint = if (selected) Color.White else MintGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Banner Carousel (Subsidy News Alert)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreen)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Abstract green decoration circle
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 35.dp, y = (-35).dp)
                            .background(MintGreen.copy(alpha = 0.25f), CircleShape)
                    )

                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "★ GOVERNMENT SUBSIDY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Get up to 33% subsidy on Sahiwal and Gir cows under Punjab Dairy Scheme 2026",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Click to calculate loan subsidy and check eligibility instantly.",
                            fontSize = 11.sp,
                            color = LightSage,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }

        // 6. Featured Animals Row
        if (featuredListings.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = LanguageHelper.get("featured_animals", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(featuredListings) { item ->
                            FeaturedCard(listing = item, onClick = { onListingClick(item) })
                        }
                    }
                }
            }
        }

        // 7. Newly Added / Listings Section Title
        item {
            Text(
                text = LanguageHelper.get("newly_added", lang) + " (" + listings.size + ")",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = ForestGreen
            )
        }

        // 8. Listings vertical list
        if (listings.isEmpty()) {
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
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = MintGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No listings match your search.", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Try resetting filters or adjusting search text.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(listings) { item ->
                NormalCard(listing = item, onClick = { onListingClick(item) })
            }
        }

        // 9. Punjab Dairy Schemes & news section (Agricultural Trust)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = LanguageHelper.get("gov_schemes", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                SchemeCard(
                    title = "National Livestock Mission (NLM)",
                    desc = "Get 50% subsidy up to ₹50 Lakhs for establishing commercial poultry, sheep/goat, and fodder seed blocks."
                )
                SchemeCard(
                    title = "Punjab Dairy Entrepreneurship Dev Program",
                    desc = "Interest subvention of 4% on livestock purchases of up to 10 milch cows for rural youth in Amritsar/Sangrur."
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = LanguageHelper.get("dairy_news", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoBorderColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ludhiana Milk Union raises procurement prices by ₹20/kg fat",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "A boost for Punjab dairy farmers! Starting next Monday, Verka will pay ₹840/kg fat, enhancing monthly profits.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedCard(
    listing: Listing,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .border(1.dp, BentoBorderColor, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Simulated Visual image box with rich color gradient placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(LightSage, MintGreen.copy(alpha = 0.15f))
                        )
                    )
            ) {
                // Top Tag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, BentoBorderColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("★ FEATURED", fontSize = 8.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                    }

                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = ForestGreen.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Bottom Overlay with category
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(ForestGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(listing.category, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "${listing.breed} ${listing.category.dropLast(1)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = ForestGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null, tint = MintGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${listing.dailyMilkYieldLiters} L/Day", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(listing.location, fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${"%,.0f".format(listing.price)}",
                        fontWeight = FontWeight.Black,
                        color = ForestGreen,
                        fontSize = 16.sp
                    )
                    Icon(Icons.Outlined.CheckCircle, contentDescription = "Verified", tint = MintGreen, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun NormalCard(
    listing: Listing,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .border(1.dp, BentoBorderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Image representation
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LightSage),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${listing.breed} ${listing.category.dropLast(1)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    if (listing.distanceKm < 15.0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(LightSage, RoundedCornerShape(6.dp))
                                .border(1.dp, ForestGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("NEARBY", fontSize = 8.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Age: ${listing.ageYears} Yrs", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    if (listing.dailyMilkYieldLiters > 0.0) {
                        Text("Yield: ${listing.dailyMilkYieldLiters}L/d", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(listing.location, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Price Column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${"%,.0f".format(listing.price)}",
                    fontWeight = FontWeight.Black,
                    color = ForestGreen,
                    fontSize = 15.sp
                )
                if (listing.isNegotiable) {
                    Text("Negotiable", fontSize = 9.sp, color = MintGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun SchemeCard(
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, BentoBorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = ForestGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium, lineHeight = 15.sp)
        }
    }
}
