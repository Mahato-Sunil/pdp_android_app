package com.officialsunil.pdpapplication.admin.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.officialsunil.pdpapplication.admin.screen.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.NavigationUtils

class AdminHome : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AdminDashboardScreen(
                    totalUsers = 21,
                    totalPredictions = 123,
                    onDiseaseDetailsClick = {
                        // navigate to the diseae details section
                        NavigationUtils.navigate(this@AdminHome, "adminCase")
                    },
                    onSettingsClick = {
                        NavigationUtils.navigate(this@AdminHome, "adminSettings")
                    }
                )
            }
        }
    }
}


@Composable
fun AdminHomeHeader() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp)
                .height(60.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                modifier = Modifier.size(30.dp)
            )

            Text(
                text = "Admin Dashboard", style = TextStyle(
                    fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 1.2.sp
                ), textAlign = TextAlign.Start, modifier = Modifier.wrapContentSize()
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}


@Composable
fun AdminDashboardScreen(
    totalUsers: Int,
    totalPredictions: Int,
    onDiseaseDetailsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AdminHomeHeader()
        },
        modifier = Modifier
            .systemBarsPadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== Animated Summary Section =====
            var visible by remember { mutableStateOf(false) }

            // Trigger animation after small delay
            LaunchedEffect(Unit) {
                delay(200)
                visible = true
            }

            AnimatedVisibility(visible = visible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SummaryCard(title = "Total Users", value = totalUsers.toString())
                    SummaryCard(title = "Predictions", value = totalPredictions.toString())
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            HorizontalDivider(
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(36.dp))

            // ===== Navigation Section =====

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                ,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedDashboardItem(
                    title = "Cases",
                    icon = Icons.Default.Info,
                    onClick = onDiseaseDetailsClick
                )
                AnimatedDashboardItem(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String) {
    // Smooth fade-in effect
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "fadeAnimation"
    )

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    Card(
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha }
            .width(150.dp), // Fixed width for uniform card
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AnimatedDashboardItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    // Animate scale when pressed
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = "scaleAnimation"
    )

    Card(
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick()
                pressed = false
            },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}