package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LightSage
import com.example.ui.theme.MintGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: PashuViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var loginTab by remember { mutableStateOf(0) } // 0: Phone OTP, 1: Email, 2: Aadhaar ID Verify
    val lang by viewModel.currentLanguage.collectAsState()

    // Inputs
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpTimer by remember { mutableStateOf(30) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var aadhaarNumber by remember { mutableStateOf("") }
    var aadhaarUploaded by remember { mutableStateOf(false) }
    var aadhaarStatus by remember { mutableStateOf("Pending Upload") } // Pending Upload, Uploading, Verifying, Verified
    var verifyProgress by remember { mutableStateOf(0f) }

    val scrollState = rememberScrollState()

    // OTP Timer countdown
    LaunchedEffect(otpSent, otpTimer) {
        if (otpSent && otpTimer > 0) {
            delay(1000)
            otpTimer--
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Aesthetic Top Agriculture Accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ForestGreen, MintGreen.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Logo / Icon
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = "PashuLink Logo",
                    tint = ForestGreen,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PashuLink",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = LanguageHelper.get("app_tagline", lang),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = LightSage,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Main Glassmorphic Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Auth Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TabItem(
                            title = "Phone OTP",
                            active = loginTab == 0,
                            icon = Icons.Default.PhoneAndroid,
                            onClick = { loginTab = 0 }
                        )
                        TabItem(
                            title = "Email",
                            active = loginTab == 1,
                            icon = Icons.Default.Email,
                            onClick = { loginTab = 1 }
                        )
                        TabItem(
                            title = "Aadhaar",
                            active = loginTab == 2,
                            icon = Icons.Default.QrCode,
                            onClick = { loginTab = 2 }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic Tabs Content
                    AnimatedContent(
                        targetState = loginTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                        },
                        label = "auth_tabs_content"
                    ) { tab ->
                        when (tab) {
                            0 -> { // Phone OTP
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Login with Mobile Number",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Enter your 10-digit phone number to receive a secure OTP.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = { if (it.length <= 10) phoneNumber = it },
                                        label = { Text("Phone Number") },
                                        placeholder = { Text("98765 43210") },
                                        prefix = { Text("+91 ") },
                                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    if (otpSent) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                            value = otpCode,
                                            onValueChange = { if (it.length <= 6) otpCode = it },
                                            label = { Text("6-Digit OTP") },
                                            placeholder = { Text("XXXXXX") },
                                            leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (otpTimer > 0) "Resend OTP in ${otpTimer}s" else "Didn't get OTP?",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (otpTimer == 0) {
                                                TextButton(onClick = {
                                                    otpTimer = 30
                                                    scope.launch {
                                                        delay(300)
                                                    }
                                                }) {
                                                    Text("Resend", fontWeight = FontWeight.Bold, color = MintGreen)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            if (!otpSent) {
                                                if (phoneNumber.length == 10) {
                                                    otpSent = true
                                                    otpTimer = 30
                                                }
                                            } else {
                                                if (otpCode.length == 6) {
                                                    viewModel.currentUserPhone.value = "+91 $phoneNumber"
                                                    onLoginSuccess()
                                                }
                                            }
                                        },
                                        enabled = (phoneNumber.length == 10 && !otpSent) || (otpSent && otpCode.length == 6),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                                    ) {
                                        Text(
                                            text = if (!otpSent) "Send Secure OTP" else "Verify & Login",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            1 -> { // Email / Google login
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Login with Email or Socials",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("Email Address") },
                                        leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            if (email.contains("@") && password.length >= 6) {
                                                viewModel.currentUserName.value = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                                                onLoginSuccess()
                                            }
                                        },
                                        enabled = email.contains("@") && password.length >= 6,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                                    ) {
                                        Text("Sign In with Password", fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // OR Separator
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        HorizontalDivider(modifier = Modifier.weight(1f))
                                        Text(
                                            text = " OR ",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        HorizontalDivider(modifier = Modifier.weight(1f))
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Google Sign-In
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                viewModel.currentUserName.value = "Google User (Amarjit)"
                                                onLoginSuccess()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MintGreen)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Continue with Google", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }
                            2 -> { // Aadhaar ID Verification
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = LanguageHelper.get("aadhaar_verify", lang),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Sellers and Vets verified with Aadhaar get a verified green tick to boost buyers' trust by 400%.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = aadhaarNumber,
                                        onValueChange = { if (it.length <= 12) aadhaarNumber = it },
                                        label = { Text("Aadhaar Number (12-Digits)") },
                                        leadingIcon = { Icon(Icons.Outlined.Fingerprint, contentDescription = null) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (!aadhaarUploaded) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MintGreen.copy(alpha = 0.05f))
                                                .clickable {
                                                    aadhaarUploaded = true
                                                    aadhaarStatus = "Uploading File..."
                                                    scope.launch {
                                                        // Simulate file upload progress
                                                        for (i in 1..10) {
                                                            delay(150)
                                                            verifyProgress = i / 10f
                                                        }
                                                        aadhaarStatus = "AI Verifying Aadhaar details..."
                                                        verifyProgress = 0f
                                                        for (i in 1..20) {
                                                            delay(100)
                                                            verifyProgress = i / 20f
                                                        }
                                                        aadhaarStatus = "Aadhaar Digitally Verified!"
                                                        viewModel.isUserVerified.value = true
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MintGreen, modifier = Modifier.size(32.dp))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Tap to Upload Aadhaar PDF/Photo", style = MaterialTheme.typography.labelMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = LightSage.copy(alpha = 0.3f))
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (aadhaarStatus.contains("Verified")) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                                        contentDescription = null,
                                                        tint = if (aadhaarStatus.contains("Verified")) ForestGreen else MintGreen
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(text = aadhaarStatus, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                }

                                                if (!aadhaarStatus.contains("Verified")) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    LinearProgressIndicator(
                                                        progress = { verifyProgress },
                                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                                                        color = ForestGreen,
                                                        trackColor = Color.LightGray
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            onLoginSuccess()
                                        },
                                        enabled = aadhaarStatus.contains("Verified") || aadhaarNumber.length == 12,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                                    ) {
                                        Text("Continue with Verification", fontWeight = FontWeight.Bold)
                                    }
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
fun RowScope.TabItem(
    title: String,
    active: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (active) ForestGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
