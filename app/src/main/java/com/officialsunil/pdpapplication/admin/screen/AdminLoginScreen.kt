package com.officialsunil.pdpapplication.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.firebase.EmailAuthUtils
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme

class AdminLoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AdminLoginScreen(
                    onLoginClick = { email, password ->
                        EmailAuthUtils.loginWithEmail(
                            email = email,
                            password = password,
                            onSuccess = { user ->
                                Toast.makeText(
                                    this,
                                    "Authentication Successfull",
                                    Toast.LENGTH_SHORT
                                ).show()
                                NavigationUtils.navigate(this, "adminHome", true)
                            },
                            onFailure = { exception ->
                                Toast.makeText(
                                    this,
                                    "Please Enter Correct Credentials",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Auth", "Failed to login", exception)
                            })
                    },
                    onBackClick = {
                        NavigationUtils.navigate(
                            context = this@AdminLoginScreen,
                            destination = "register",
                            finish = true
                        )
                    }
                )

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Title
            Text(
                text = "Admin Login",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Login Button
            OutlinedButton(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !(email.isEmpty() || password.isEmpty()),
            ) {
                Text("Login", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back Button
            OutlinedButton(
                onClick = { onBackClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Back to Client Screen", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

