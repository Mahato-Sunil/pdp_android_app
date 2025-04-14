package com.officialsunil.pdpapplication.viewui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.EmailAuthUtils
import com.officialsunil.pdpapplication.utils.GoogleAuthUtils
import com.officialsunil.pdpapplication.utils.RegistrationCredentials
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme

class AccountRegistrationActivity : ComponentActivity() {
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AccountRegistrationUI(
                    initiateGoogleSignin = { initateGoogleSignin() },
                    initiateEmailSignin = { name, email, password ->
                        initiateEmailSignin(name, email, password)
                    })
            }
        }
    }

    // function to perform google sign in
    private fun initateGoogleSignin() {
        googleSignInLauncher = GoogleAuthUtils.getGoogleSignInLauncher(this) { success ->
            if (success) GoogleAuthUtils.initiateGoogleSignin(
                context = this,
                scope = this.lifecycleScope,
                launcher = googleSignInLauncher,
                googleLogin = {
                    Log.d("Account Register", "Account Registeration Successful")
                    Toast.makeText(this, "Successfully Authenticated", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                })
        }
    }

    //email signin
    private fun initiateEmailSignin(name: String, email: String, password: String) {
        EmailAuthUtils.registerWithEmail(
            context = this,
            name = name,
            email = email,
            password = password,
            onSuccess = {
                Log.d("Account Register", "Account Registeration Successful")
                Toast.makeText(this, "Successfully Authenticated", Toast.LENGTH_SHORT).show()
                navigateToHome()
            },
            onFailure = {
                Log.d("Account Register", "Account Registeration Failed")
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            })
    }

    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }
}

// main container
@Composable
fun AccountRegistrationUI(
    initiateGoogleSignin: () -> Unit, initiateEmailSignin: (String, String, String) -> Unit
) {
    Scaffold(
        topBar = { AccountHeaderUI() },
        modifier = Modifier
            .systemBarsPadding()
            .background(colorResource(R.color.light_background))
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            FormContentUI(initiateGoogleSignin, initiateEmailSignin)
        }
    }
}

@Composable
fun AccountHeaderUI() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(colorResource(R.color.light_background))
        ) {
            Text(
                text = "Create New Account",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 1.2.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 16.dp)
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
fun FormContentUI(
    initiateGoogleSignin: () -> Unit, initiateEmailSignin: (String, String, String) -> Unit
) {
//    var inputField by remember { mutableStateOf("") }
    var isChanged by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        val formContent = getFormUI()
        formContent.forEach { item ->

            Text(
                text = item.heading,
                style = TextStyle(
                    fontWeight = FontWeight.Normal, fontSize = 18.sp, letterSpacing = 1.2.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )

            OutlinedTextField(
                value = when (item.key.lowercase()) {
                    "name" -> name
                    "email" -> email
                    "password" -> password
                    "confirmpassword" -> confirmPassword
                    else -> ""
                }, onValueChange = {
                    when (item.key.lowercase()) {
                        "name" -> name = it
                        "email" -> email = it
                        "password" -> password = it
                        "confirmpassword" -> confirmPassword = it
                    }
                    isChanged = true
                }, modifier = Modifier.fillMaxWidth(.95f), singleLine = true, textStyle = TextStyle(
                    fontSize = 14.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.2.sp
                ), placeholder = {
                    Text(
                        text = item.placeholder,
                        color = Color.Gray,
                    )
                },

                visualTransformation = if (item.inputType != KeyboardType.Password) VisualTransformation.None
                else if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                trailingIcon = {
                    if (isChanged) {
                        //for password icons
                        val icon = if (item.inputType != KeyboardType.Password) Icons.Default.Close
                        else {
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        }

                        IconButton(
                            onClick = {
                                if (item.inputType != KeyboardType.Password) when (item.key.lowercase()) {
                                    "name" -> name = ""
                                    "email" -> email = ""
                                    "password" -> password = ""
                                    "confirmpassword" -> confirmPassword = ""
                                }
                                else passwordVisible != passwordVisible
                            }) {
                            Icon(
                                imageVector = icon, contentDescription = item.heading
                            )
                        }
                    }
                },

                shape = RoundedCornerShape(20.dp), keyboardOptions = KeyboardOptions(
                    keyboardType = item.inputType, imeAction = ImeAction.Next
                )
            )
        }

        // buttons
        Button(
            onClick = {
                initiateEmailSignin(name, email, password)
            },
            colors = ButtonColors(
                containerColor = Color(105, 146, 255, 255),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFD8D8D8),
                disabledContentColor = Color(0xFF575757)
            ),
            enabled = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .border(
                    width = 1.dp, color = Color.Transparent, shape = RoundedCornerShape(size = 8.dp)
                )
                .fillMaxWidth(.8f)
                .height(45.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(

                text = "Get Register",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "--------------------  OR  --------------------", style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight(800),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        )


        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                initiateGoogleSignin()
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
                    width = 1.dp, color = Color(0xFF000000), shape = RoundedCornerShape(size = 8.dp)
                )
                .fillMaxWidth(.8f)
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
                text = "Continue With Google", style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(700),
                    color = colorResource(id = R.color.font_color),
                ), modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }
}


//for the input form
fun getFormUI(): List<RegistrationCredentials> {
    return listOf(
        RegistrationCredentials(
           key = "name", heading = "Full Name", placeholder = "Full Name", inputType = KeyboardType.Text
        ),

        RegistrationCredentials(
            key = "email", heading = "Email", placeholder = "Email Id", inputType = KeyboardType.Email
        ),

        RegistrationCredentials(
         key ="password",   heading = "Password", placeholder = "Password", inputType = KeyboardType.Password
        ),

        RegistrationCredentials(
            key = "confirmPassword",
            heading = "Confirm Password",
            placeholder = "Retype Password",
            inputType = KeyboardType.Password
        )
    )
}
