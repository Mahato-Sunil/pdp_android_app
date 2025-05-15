package com.officialsunil.pdpapplication.viewui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.GoogleAuthUtils
import com.officialsunil.pdpapplication.utils.SigninigRationale
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme

class GoogleSignInRationale : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                ShowRationaleUI(
                    navigateToHome = { navigateToHome() },
                    initGoogleLogin = { initGoogleLogin() })
            }
        }
    }

    //function to navigate to the home intent
    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    // function to initiate google login
    // launcher for the google authentication
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) Log.d(
                "GoogleAuth", "Account picker returned successfully."
            )
            else Log.w("GoogleAuth", "Account picker was canceled.")
        }

    private fun initGoogleLogin() {
        GoogleAuthUtils.initiateGoogleSignin(
            context = this,
            scope = lifecycleScope,
            launcher = googleSignInLauncher,
            googleLogin = {
                runOnUiThread {
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeIntent)
                    Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
    }

}

// code for showing the permission rationale
@Composable
fun ShowRationaleUI(
    navigateToHome: () -> Unit,
    initGoogleLogin: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(8.dp)
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.permission_rationale),
            contentDescription = "Rationale Image",
            modifier = Modifier
                .fillMaxWidth(.7f)
                .wrapContentHeight(),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Think Again !", style = TextStyle(
                color = colorResource(R.color.font_color),
                fontSize = 30.sp,
                fontWeight = FontWeight(500),
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(10.dp))
        RationaleDescription()
        Spacer(Modifier.height(20.dp))
        BottomNavigationButton(navigateToHome, initGoogleLogin)
    }
}

// description
@Composable
fun RationaleDescription() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "With Account you can", style = TextStyle(
                color = colorResource(R.color.font_color),
                fontSize = 18.sp,
                fontWeight = FontWeight(400),
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
        )

        val descriptionList = populateSigningRationale()

        descriptionList.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth(.24f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = "Check Icon",
                        tint = Color.Green,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 5.dp)
                    )
                }

                Box(
                    contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.description.toString(), style = TextStyle(
                            color = colorResource(R.color.font_color),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.2.sp,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
        }
    }
}


// bottom buttons
@Composable
fun BottomNavigationButton(
    navigateToHome: () -> Unit,
    initGoogleLogin: () -> Unit
) {

    OutlinedButton(
        shape = RoundedCornerShape(16.dp), colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = colorResource(R.color.font_color),
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.LightGray
        ), onClick = {
            // go to the home activity
            navigateToHome()
        }, modifier = Modifier
            .fillMaxWidth(.8f)
            .height(50.dp)
    ) {
        Text(
            text = "I'll Do Later"
        )
    }

    Spacer(Modifier.height(5.dp))
    OutlinedButton(
        shape = RoundedCornerShape(16.dp), colors = ButtonColors(
            containerColor = Color(175, 211, 241, 255),
            contentColor = colorResource(R.color.font_color),
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.LightGray
        ), onClick = {
            // start google sign in intent
            initGoogleLogin()
        }, modifier = Modifier
            .fillMaxWidth(.8f)
            .height(50.dp)
    ) {
        Text(
            text = "Sign In"
        )
    }
}

// function to  get the sign in rational
fun populateSigningRationale(): List<SigninigRationale> {
    val descriptionList = listOf(
        SigninigRationale("Keep Track of Diseases"),
        SigninigRationale("Access your data across the devices"),
        SigninigRationale("Prediction  Based on History")
    )

    return descriptionList
}