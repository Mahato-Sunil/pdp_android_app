package com.officialsunil.pdpapplication.viewui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.SplashScreenUtils
import com.officialsunil.pdpapplication.utils.firebase.EmailAuthUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials.isAdmin
import com.officialsunil.pdpapplication.utils.firebase.GoogleAuthUtils

class MainActivity : ComponentActivity() {
    // give reference to the view model
    override fun onCreate(savedInstanceState: Bundle?) {
        // Check if splash should be skipped
        val skipSplash = intent.getBooleanExtra("SKIP_SPLASH", false)
        super.onCreate(savedInstanceState)
        if (!skipSplash) SplashScreenUtils.initializeSplashScreen(
            activity = this, onComplete = { showMainUIContent() })
        else showMainUIContent()
    }

    // function to show the main ui content
    private fun showMainUIContent() {
        setContent {
            PDPApplicationTheme {
                if (isUserLoggedIn()) {
                    // check for admin or current user
                    Log.d("Admin", "Admin : ${isAdmin()}")
                    if (isAdmin())

                        NavigationUtils.navigate(this, "adminHome", true)
                    else NavigationUtils.navigate(this, "home", true)
                } else {
                    InitMainActivityUI(
                        context = this,
                        activity = this,
                        initGoogleLogin = { initGoogleLogin() },
                        initEmailPasswordSignin = { email, password ->
                            initEmailPasswordSignin(
                                email, password
                            )
                        })
                }
            }
        }
    }

    // backend logics
    private fun initEmailPasswordSignin(email: String, password: String) {
        val adminMail = "sunil@admin.com"
        if (email == adminMail) Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG)
            .show()
        else EmailAuthUtils.loginWithEmail(email = email, password = password, onSuccess = { user ->
            Toast.makeText(this, "Authentication Successfull", Toast.LENGTH_SHORT).show()
            NavigationUtils.navigate(this, "home", true)
        }, onFailure = { exception ->
            Toast.makeText(this, "Please Enter Correct Credentials", Toast.LENGTH_SHORT).show()
            Log.e("Auth", "Failed to login", exception)
        })
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) Log.d(
                "GoogleAuth", "Account picker returned successfully."
            )
            else Log.w("GoogleAuth", "Account picker was canceled.")
        }

    private fun initGoogleLogin() {
        GoogleAuthUtils.initiateGoogleSignin(
            context = this, scope = lifecycleScope, launcher = googleSignInLauncher, googleLogin = {
                runOnUiThread {
                    NavigationUtils.navigate(this, "home", true)
                }
            })
    }

    private fun isUserLoggedIn(): Boolean {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser // it was auth.getCurrentUser() in older versions
        return user != null
    }

    @Composable
    fun InitMainActivityUI(
        context: Context,
        activity: Activity,
        initGoogleLogin: () -> Unit,
        initEmailPasswordSignin: (String, String) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Layout(context, activity, initGoogleLogin, initEmailPasswordSignin)
        }
    }

    @Composable
    fun Layout(
        context: Context,
        activity: Activity,
        initGoogleLogin: () -> Unit,
        initEmailPasswordSignin: (String, String) -> Unit
    ) {
        Column {
            HeadingUI()
            MainContainer(
                context,
                activity,
                initGoogleLogin,
                initEmailPasswordSignin,
            )
        }
    }

    @Composable
    fun HeadingUI() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pdp_logo_text),
                contentDescription = "Potato Disease Prediction Logo ",
                modifier = Modifier.fillMaxSize()
            )

        }
    }

    @Composable
    fun MainContainer(
        context: Context,
        activity: Activity,
        initGoogleLogin: () -> Unit,
        initEmailPasswordSignin: (String, String) -> Unit,
    ) {
        var emailInpt by remember { mutableStateOf("") }
        var passwordInpt by remember { mutableStateOf("") }
        var isChanged by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "WELCOME ", style = TextStyle(
                    fontSize = 36.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight(800),
                    color = colorResource(R.color.font_color),
                    textAlign = TextAlign.Center
                ), letterSpacing = 7.5.sp, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            // custom email and password login
            OutlinedTextField(
                value = emailInpt, onValueChange = {
                emailInpt = it
                isChanged = true
            }, modifier = Modifier.fillMaxWidth(.95f), singleLine = true, textStyle = TextStyle(
                fontSize = 14.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.2.sp
            ), placeholder = {
                Text(
                    text = "Email",
                    color = Color.Gray,
                )
            }, trailingIcon = {
                if (isChanged) IconButton(
                    onClick = { emailInpt = "" }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close icon",
                    )
                }
            }, shape = RoundedCornerShape(20.dp), keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = passwordInpt,
                onValueChange = {
                    passwordInpt = it
                    isChanged = true
                },
                modifier = Modifier.fillMaxWidth(.95f),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.2.sp
                ),
                placeholder = {
                    Text(
                        text = "Password",
                        color = Color.Gray,
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (isChanged) {
                        val icon =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {
                    initEmailPasswordSignin(emailInpt, passwordInpt)
                },
                colors = ButtonColors(
                    containerColor = Color(99, 206, 255, 255),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFD8D8D8),
                    disabledContentColor = Color(0xFF575757)
                ),
                enabled = !(emailInpt.isEmpty() || passwordInpt.isEmpty()),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF000000),
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .width(296.dp)
                    .height(45.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(

                    text = "Sign in",
                    style = TextStyle(
                        fontSize = 14.sp, fontWeight = FontWeight(700), letterSpacing = 1.2.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(123.50195.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Create New Account",
                style = TextStyle(
                    fontSize = 16.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.2.sp
                ),
                textDecoration = TextDecoration.Underline,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    EmailAuthUtils.navigateToRegistrationActivity(context, activity)
                })

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = {
                    initGoogleLogin()
                },
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = colorResource(id = R.color.font_color),
                    disabledContainerColor = Color(0xFFD8D8D8),
                    disabledContentColor = Color(0xFF575757)
                ),
                enabled = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF000000),
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .width(296.dp)
                    .height(45.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google Icon",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .padding(1.dp)
                        .width(23.62646.dp)
                        .height(22.51563.dp)
                )

                // vertical spacer
                Spacer(modifier = Modifier.width(21.dp))
                Text(

                    text = "Sign in with Google",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(700),
                        color = colorResource(id = R.color.font_color),
                    ),
                    modifier = Modifier
                        .width(123.50195.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "--------------------  OR  --------------------", style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight(800),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            )


            Spacer(modifier = Modifier.height(25.dp))
            Button(
                onClick = {
                    NavigationUtils.navigate(context, "signinRationale", true)
                },
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = colorResource(id = R.color.font_color),
                    disabledContainerColor = Color(0xFFD8D8D8),
                    disabledContentColor = Color(0xFF575757)
                ),
                enabled = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF000000),
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .width(296.dp)
                    .height(45.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Google Icon",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .padding(1.dp)
                        .width(23.62646.dp)
                        .height(22.51563.dp)

                )

                // vertical spacer
                Spacer(modifier = Modifier.width(21.dp))
                Text(
                    text = "Skip For Now",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(700),
                        color = colorResource(id = R.color.font_color),
                    ),
                    modifier = Modifier
                        .width(123.50195.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.height(32.dp))

            TextButton(
                onClick = {
                    NavigationUtils.navigate(context, "adminLogin")
                    Log.d("Admin", "Button in Main Activity Clicked")
                }) {
                Text("Admin Login")
            }
        }
    }
}