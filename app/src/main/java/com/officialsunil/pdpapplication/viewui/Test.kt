package com.officialsunil.pdpapplication.viewui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.FirebaseUserCredentials
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme

class Test : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
//    val currentUsersCredentials = FirebaseUserCredentials.getCurrentUserCredentails()
//
//    val blankImageUrl = "https://cdn.pixabay.com/photo/2016/04/22/04/57/graduation-1345143_1280.png"
//    val photoUrl = currentUsersCredentials?.photoUrl
//    val isEmailVerified = currentUsersCredentials?.isEmailVerified

    Spacer(Modifier.height(20.dp))

    Box(
        modifier = Modifier
            .size(150.dp) // Same size as your image
            .padding(8.dp)
    ) {
        // Profile image
        Image(
            painter = painterResource(R.drawable.permission_rationale),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(2.dp, color = Color.Black, shape = CircleShape)
        )

        // Green tick overlay at bottom-right
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Verified",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-4).dp)
                .background(Color.Green, CircleShape)
        )
    }


    Spacer(Modifier.height(50.dp))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    PDPApplicationTheme {
        Greeting2("Android")
    }
}