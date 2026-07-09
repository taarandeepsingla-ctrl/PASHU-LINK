package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Listing
import com.example.ui.*
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PaleLime

sealed class AppScreen {
    object Auth : AppScreen()
    object Home : AppScreen()
    class ListingDetail(val listing: Listing) : AppScreen()
    object SellerDashboard : AppScreen()
    object VetDashboard : AppScreen()
    object AdminDashboard : AppScreen()
    object AiTools : AppScreen()
    object Map : AppScreen()
    object Transport : AppScreen()
    object DairyStore : AppScreen()
    class Chat(val sessionId: String) : AppScreen()
    object Profile : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: PashuViewModel = viewModel()
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Auth) }

                val userRole by viewModel.currentRole.collectAsState()
                val lang by viewModel.currentLanguage.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Display bottom bar only if we are NOT on the Auth screen
                        if (currentScreen != AppScreen.Auth) {
                            PashuBottomBar(
                                activeRole = userRole,
                                currentScreen = currentScreen,
                                onNavigate = { screen -> currentScreen = screen }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                            },
                            label = "screen_routing"
                        ) { screen ->
                            when (screen) {
                                is AppScreen.Auth -> {
                                    AuthScreen(
                                        viewModel = viewModel,
                                        onLoginSuccess = {
                                            currentScreen = when (userRole) {
                                                "Seller" -> AppScreen.SellerDashboard
                                                "Veterinarian" -> AppScreen.VetDashboard
                                                "Admin" -> AppScreen.AdminDashboard
                                                else -> AppScreen.Home
                                            }
                                        }
                                    )
                                }
                                is AppScreen.Home -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onListingClick = { listing -> currentScreen = AppScreen.ListingDetail(listing) }
                                    )
                                }
                                is AppScreen.ListingDetail -> {
                                    AnimalDetailScreen(
                                        listing = screen.listing,
                                        viewModel = viewModel,
                                        onBack = { currentScreen = AppScreen.Home },
                                        onNavigateToChat = { session -> currentScreen = AppScreen.Chat(session) },
                                        onNavigateToTransport = { currentScreen = AppScreen.Transport },
                                        onNavigateToVet = { currentScreen = AppScreen.VetDashboard }
                                    )
                                }
                                is AppScreen.SellerDashboard -> {
                                    SellerScreen(
                                        viewModel = viewModel,
                                        onListingClick = { listing -> currentScreen = AppScreen.ListingDetail(listing) }
                                    )
                                }
                                is AppScreen.VetDashboard -> {
                                    VetScreen(viewModel = viewModel)
                                }
                                is AppScreen.AdminDashboard -> {
                                    AdminScreen(viewModel = viewModel)
                                }
                                is AppScreen.AiTools -> {
                                    AiToolsScreen(viewModel = viewModel)
                                }
                                is AppScreen.Map -> {
                                    MapScreen(viewModel = viewModel)
                                }
                                is AppScreen.Transport -> {
                                    TransportScreen(viewModel = viewModel)
                                }
                                is AppScreen.DairyStore -> {
                                    DairyStoreScreen(viewModel = viewModel)
                                }
                                is AppScreen.Chat -> {
                                    ChatScreen(
                                        sessionId = screen.sessionId,
                                        viewModel = viewModel,
                                        onBack = {
                                            currentScreen = when (userRole) {
                                                "Seller" -> AppScreen.SellerDashboard
                                                "Veterinarian" -> AppScreen.VetDashboard
                                                "Admin" -> AppScreen.AdminDashboard
                                                else -> AppScreen.Home
                                            }
                                        }
                                    )
                                }
                                is AppScreen.Profile -> {
                                    ProfileScreen(
                                        viewModel = viewModel,
                                        onLogout = { currentScreen = AppScreen.Auth }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PashuBottomBar(
    activeRole: String,
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Universal Left Options
        if (activeRole == "Buyer") {
            NavigationBarItem(
                selected = currentScreen is AppScreen.Home,
                onClick = { onNavigate(AppScreen.Home) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", fontSize = 10.sp) }
            )
            NavigationBarItem(
                selected = currentScreen is AppScreen.Map,
                onClick = { onNavigate(AppScreen.Map) },
                icon = { Icon(Icons.Default.Map, contentDescription = "Locator") },
                label = { Text("Map", fontSize = 10.sp) }
            )
            NavigationBarItem(
                selected = currentScreen is AppScreen.DairyStore,
                onClick = { onNavigate(AppScreen.DairyStore) },
                icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Store") },
                label = { Text("Dairy Store", fontSize = 10.sp) }
            )
        } else if (activeRole == "Seller") {
            NavigationBarItem(
                selected = currentScreen is AppScreen.SellerDashboard,
                onClick = { onNavigate(AppScreen.SellerDashboard) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Seller Home") },
                label = { Text("Dashboard", fontSize = 10.sp) }
            )
        } else if (activeRole == "Veterinarian") {
            NavigationBarItem(
                selected = currentScreen is AppScreen.VetDashboard,
                onClick = { onNavigate(AppScreen.VetDashboard) },
                icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Vet Home") },
                label = { Text("Vet Desk", fontSize = 10.sp) }
            )
        } else if (activeRole == "Admin") {
            NavigationBarItem(
                selected = currentScreen is AppScreen.AdminDashboard,
                onClick = { onNavigate(AppScreen.AdminDashboard) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Home") },
                label = { Text("Admin", fontSize = 10.sp) }
            )
        }

        // Universal AI and logistics triggers for easy navigation
        NavigationBarItem(
            selected = currentScreen is AppScreen.AiTools,
            onClick = { onNavigate(AppScreen.AiTools) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assit", tint = MintGreen) },
            label = { Text("AI Tools", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen) }
        )

        NavigationBarItem(
            selected = currentScreen is AppScreen.Transport,
            onClick = { onNavigate(AppScreen.Transport) },
            icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Truck Booking") },
            label = { Text("Transport", fontSize = 10.sp) }
        )

        NavigationBarItem(
            selected = currentScreen is AppScreen.Profile,
            onClick = { onNavigate(AppScreen.Profile) },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "My Profile") },
            label = { Text("Profile", fontSize = 10.sp) }
        )
    }
}

@Composable
fun ProfileScreen(
    viewModel: PashuViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name by viewModel.currentUserName.collectAsState()
    val phone by viewModel.currentUserPhone.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val isVerified by viewModel.isUserVerified.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Aesthetic Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ForestGreen, MintGreen)
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    if (isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = "Verified Profile", tint = PaleLime, modifier = Modifier.size(20.dp))
                    }
                }

                Text(phone, color = LightSage, style = MaterialTheme.typography.bodyMedium)
                
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(PaleLime, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Role: ${role.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // 1. Interactive Role Switcher Panel (Critical feature validation!)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = MintGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Emulator Profile Switcher", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall, color = ForestGreen)
                    }
                    Text(
                        text = "Tap on any role to immediately switch dashboards and explore matching views:",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    listOf("Buyer", "Seller", "Veterinarian", "Admin").forEach { roleOption ->
                        val active = role == roleOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) PaleLime.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable {
                                    viewModel.currentRole.value = roleOption
                                    viewModel.currentUserName.value = when (roleOption) {
                                        "Seller" -> "Sardar Gurcharan Singh"
                                        "Veterinarian" -> "Dr. Amanpreet Singh"
                                        "Admin" -> "Director Sandhu"
                                        else -> "Sardar Amarjit Singh"
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(roleOption, fontWeight = FontWeight.Bold, color = if (active) ForestGreen else MaterialTheme.colorScheme.onSurface)
                            if (active) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = MintGreen)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Personal Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Farm Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = ForestGreen)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileDetailRow(label = "Primary Location", value = "Ludhiana District, Punjab")
                    ProfileDetailRow(label = "Dairy Co-op ID", value = "MILK-FED-PB-9883")
                    ProfileDetailRow(label = "Preferred Language", value = lang)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout from PashuLink", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
