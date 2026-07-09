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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MedicalServices
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
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import androidx.compose.foundation.border
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PaleLime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetScreen(
    viewModel: PashuViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val bookings by viewModel.bookingsList.collectAsState()
    val role by viewModel.currentRole.collectAsState()

    val vetBookings = bookings.filter { it.type == "Veterinary" || it.type == "Vaccination" }
    
    var showBookModal by remember { mutableStateOf(false) }
    var selectedVet by remember { mutableStateOf("Dr. Amanpreet Singh (Ludhiana)") }
    var serviceType by remember { mutableStateOf("Veterinary Checkup") }
    var date by remember { mutableStateOf("2026-07-15") }
    var timeSlot by remember { mutableStateOf("11:00 AM - 12:30 PM") }
    var detailNotes by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val vetsList = listOf(
        "Dr. Amanpreet Singh (B.V.Sc & A.H) - Ludhiana Hub",
        "Dr. Gurmeet Singh Sandhu (M.V.Sc) - Amritsar Veterinary",
        "Dr. Satnam Kaur (Specialist) - Jalandhar Livestock Clinic",
        "Dr. Rajbir Bajwa (Veterinary Surgeon) - Patiala Unit"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Header Banner
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
                        text = "Veterinary Digital Health Record System",
                        style = MaterialTheme.typography.titleSmall,
                        color = LightSage,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (role == "Veterinarian") "Doctor's Consultation Panel" else "Health & Vaccination Tracker",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox(title = "Health Records", value = "9 Active", icon = Icons.Default.MedicalServices, modifier = Modifier.weight(1f))
                        StatBox(title = "Vaccines Log", value = "100% OK", icon = Icons.Default.HealthAndSafety, modifier = Modifier.weight(1f))
                        StatBox(title = "Consultations", value = "${vetBookings.size} Booked", icon = Icons.Default.CalendarToday, modifier = Modifier.weight(1.2f))
                    }
                }
            }
        }

        // 2. Action buttons for Farmers
        if (role != "Veterinarian") {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = MintGreen, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Schedule Vet Visit / Vaccines",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        }
                        Text(
                            text = "Book certified veterinarians across Punjab for general health reviews, pregnancy scans, and ear tagging.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Button(
                            onClick = { showBookModal = !showBookModal },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Book Certified Vet Consultation", fontWeight = FontWeight.Bold)
                        }

                        AnimatedVisibility(visible = showBookModal) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                Text("Select Veterinarian:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                var expandVets by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(onClick = { expandVets = true }, modifier = Modifier.fillMaxWidth()) {
                                        Text(selectedVet, color = ForestGreen, fontWeight = FontWeight.Bold)
                                    }
                                    DropdownMenu(expanded = expandVets, onDismissRequest = { expandVets = false }) {
                                        vetsList.forEach { vet ->
                                            DropdownMenuItem(text = { Text(vet) }, onClick = {
                                                selectedVet = vet
                                                expandVets = false
                                            })
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Service Required:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Checkup", "Vaccination", "AI Insemination").forEach { item ->
                                        ElevatedFilterChip(
                                            selected = serviceType == item,
                                            onClick = { serviceType = item },
                                            label = { Text(item) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = date,
                                        onValueChange = { date = it },
                                        label = { Text("Visit Date") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = timeSlot,
                                        onValueChange = { timeSlot = it },
                                        label = { Text("Time Slot") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = detailNotes,
                                    onValueChange = { detailNotes = it },
                                    label = { Text("Describe Livestock Symptoms (e.g. fever, low feed intake)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (detailNotes.isNotEmpty()) {
                                            viewModel.bookVetOrTransport(
                                                type = if (serviceType == "Vaccination") "Vaccination" else "Veterinary",
                                                provider = selectedVet.substringBefore(" -"),
                                                date = date,
                                                slot = timeSlot,
                                                details = "$serviceType: $detailNotes",
                                                price = 600.0
                                            )
                                            detailNotes = ""
                                            showBookModal = false
                                        }
                                    },
                                    enabled = detailNotes.isNotEmpty(),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                                ) {
                                    Text("Confirm Booking & Pay ₹600", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Digital Health Records Tracker
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "My Digital Vaccination Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                VaccineTrackerCard(title = "Foot and Mouth Disease (FMD)", dueDate = "Due in 15 days", status = "Pending Booster", color = Color.Red)
                VaccineTrackerCard(title = "Brucellosis (Infectious Abortion)", dueDate = "Administered", status = "Completed (Valid till 2027)", color = ForestGreen)
                VaccineTrackerCard(title = "Hemorrhagic Septicemia (HS)", dueDate = "Administered", status = "Completed", color = ForestGreen)
            }
        }

        // 4. Appointment list
        item {
            Text(
                text = if (role == "Veterinarian") "My Patient Checkups" else "My Vet Bookings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = ForestGreen
            )
        }

        if (vetBookings.isEmpty()) {
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
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = MintGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No active veterinary consults.", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(vetBookings) { booking ->
                BookingVetCard(booking = booking, onRate = { rating -> viewModel.rateBooking(booking.id, rating) })
            }
        }
    }
}

@Composable
fun VaccineTrackerCard(
    title: String,
    dueDate: String,
    status: String,
    color: Color
) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(dueDate, fontSize = 11.sp, color = Color.Gray)
                }
            }

            Box(
                modifier = Modifier
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun BookingVetCard(
    booking: Booking,
    onRate: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, BentoBorderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = ForestGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(booking.providerName, fontWeight = FontWeight.Black, fontSize = 13.sp, color = ForestGreen)
                }

                Box(
                    modifier = Modifier
                        .background(LightSage, RoundedCornerShape(6.dp))
                        .border(1.dp, ForestGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(booking.status, fontSize = 9.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(booking.details, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${booking.date} at ${booking.timeSlot}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "₹${booking.price}",
                    fontWeight = FontWeight.Black,
                    color = ForestGreen,
                    fontSize = 15.sp
                )
            }

            if (booking.status == "Confirmed" || booking.status == "Completed") {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BentoBorderColor)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rate your consultation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Row {
                        for (star in 1..5) {
                            Icon(
                                imageVector = if (star <= booking.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = PaleLime,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onRate(star) }
                            )
                        }
                    }
                }
            }
        }
    }
}
