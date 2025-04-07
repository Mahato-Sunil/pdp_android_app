package com.officialsunil.pdpapplication.viewui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.GoogleAuthUtils
import com.officialsunil.pdpapplication.utils.PdpModelController

class MainActivity : ComponentActivity() {
    // give reference to the view model
    private val pdpModelController by viewModels<PdpModelController>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !pdpModelController.isModelReady.value
            }

            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 1f, 0.0f)
                val zoomY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 1f, 0.0f)

                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()

                zoomX.duration = 500L
                zoomY.duration = 500L

                val animatorSet = AnimatorSet()
                animatorSet.playTogether(zoomX, zoomY)

                animatorSet.doOnEnd { screen.remove() }
                animatorSet.start() // Start the animations
            }

        }
        setContent {
            PDPApplicationTheme {
                InitMainActivityUI(navigateToHome = { navigateToHome() },
                    initGoogleLogin = { initGoogleLogin() })
            }
        }
    }

    // backend logics
    // function to go to main activity
    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    // function to initiate google login
    // launcher for the goolge authentication
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
                    navigateToHome()
                    Toast.makeText(this, "Authentication Successfull", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

@Composable
fun InitMainActivityUI(navigateToHome: () -> Unit, initGoogleLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .background(color = colorResource(R.color.light_background))
            .fillMaxSize()
    ) {
        Layout(navigateToHome, initGoogleLogin)
    }
}

@Composable
fun Layout(navigateToHome: () -> Unit, initGoogleLogin: () -> Unit) {
    Column {
        HeadingUI()
        MainContainer(navigateToHome, initGoogleLogin)
    }
}

@Composable
fun HeadingUI() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pdp_logo_text),
            contentDescription = "Potato Disease Prediction Logo ",
            modifier = Modifier.fillMaxSize()
        )

    }
}

@Composable
fun MainContainer(navigateToHome: () -> Unit, initGoogleLogin: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "WELCOME ", style = TextStyle(
                fontSize = 40.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight(800),
                color = colorResource(R.color.font_color),
                textAlign = TextAlign.Center
            ), letterSpacing = 7.5.sp, modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Start By Registering >3 ", style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400),
                color = colorResource(R.color.font_color),
                textAlign = TextAlign.Center
            ), letterSpacing = 1.5.sp, modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = {
                initGoogleLogin()
            },
            colors = ButtonColors(
                containerColor = colorResource(id = R.color.light_background),
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
                navigateToHome()
            },
            colors = ButtonColors(
                containerColor = colorResource(id = R.color.light_background),
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

    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewUI() {
//    InitMainActivityUI()
//}