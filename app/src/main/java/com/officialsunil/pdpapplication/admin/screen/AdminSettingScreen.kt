package com.officialsunil.pdpapplication.admin.screen

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.NavigationUtils

class AdminSettingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AdminSettingScreenUI()
            }
        }
    }
}

@Composable
fun AdminSettingScreenUI() {
    Scaffold(
        topBar = { AdminSettingHeader() },
        modifier = Modifier
            .systemBarsPadding()
            .background(colorResource(R.color.light_background))
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            AdminSettingContainerUI(
                modifier = Modifier.padding(innerPadding), context = LocalContext.current
            )
        }
    }
}

// header
@Composable
fun AdminSettingHeader() {
    val context = LocalContext.current
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(60.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "adminHome", true)
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Arrow Back",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Settings", style = TextStyle(
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

// container for admin details
@Composable
fun AdminSettingContainerUI(modifier: Modifier = Modifier, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Admin Details", style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black
            ), modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        // modern rows for each detail
        DetailRow(
            icon = Icons.Default.Person, label = "Name", value = user?.displayName ?: "Not set"
        )
        DetailRow(
            icon = Icons.Default.Email, label = "Email", value = user?.email ?: "Not available"
        )
        DetailRow(icon = Icons.Default.Fingerprint, label = "UID", value = user?.uid ?: "N/A")

        Spacer(modifier = Modifier.weight(1f))

        // logout button
        Button(
            onClick = {
                auth.signOut()
                NavigationUtils.navigate(context, "register", true)
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth(.9f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F), contentColor = Color.White
            )
        ) {
            Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0xFFF6F6F6), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label, style = TextStyle(
                    fontSize = 14.sp, color = Color.Gray
                )
            )
            Text(
                text = value, style = TextStyle(
                    fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black
                )
            )
        }
    }
}
