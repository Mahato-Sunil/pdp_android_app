package com.officialsunil.pdpapplication.viewui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                InitHomeActivityUI(
                    initCameraActivity = { navigateToCameraActivity() },
                    initAccountCenter = { navigateToAccountCenter() })
            }
        }
    }

    /* ========================================================================
    Backends and Logics
    ========================================================================
    */

    // function to move to the camera activity
    fun navigateToCameraActivity() {
        val cameraIntent = Intent(this, CameraActivity::class.java)
        startActivity(cameraIntent)
        finish()
    }

    //    function to open the account center
    fun navigateToAccountCenter() {
        val accountIntent = Intent(this, AccountCenterActivity::class.java)
        startActivity(accountIntent)
    }
}

//  activity layout
@Composable
fun InitHomeActivityUI(
    initCameraActivity: () -> Unit, initAccountCenter: () -> Unit
) {
    Scaffold(
        topBar = { HomeHeadingUI() },
        bottomBar = {
        HomeButtonContainer(
            initCameraActivity, initAccountCenter
        )
    }) { innerPadding ->
        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            HomeContainer()
        }
    }

}


/* ========================================================================
    UI Layout and Composable functions
   ========================================================================
 */
@Composable
fun HomeHeadingUI() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
//            .background(Color.Blue)
//            .background(colorResource(R.color.light_background))
    ) {
        Text(
            text = "Potato Disease Prediction",
            style = TextStyle(
                color = colorResource(R.color.font_color),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(8.dp)
        )

        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            color = Color.LightGray
        )
    }
}

@Composable
fun HomeContainer() {
    val localContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 20.dp)
                .height(35.dp)
        ) {
            Text(
                text = "Your diagnoses",
                style = TextStyle(
                    color = colorResource(R.color.font_color),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                ),
            )

            Text(
                text = "View All", style = TextStyle(
                    color = Color.Blue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.2.sp,
                    textDecoration = TextDecoration.Underline
                ), modifier = Modifier.clickable {
                    val diagnosesIntent = Intent(localContext, DiagnosesListActivity::class.java)
                    localContext.startActivity(diagnosesIntent)
                })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .background(colorResource(R.color.light_card_background))
                .clickable {
                    //pass the path and the  prediction result
                    val predictionIntent = Intent(localContext, PredictionActivity::class.java)
                    localContext.startActivity(predictionIntent)
                }) {
            Image(
                painter = painterResource(R.drawable.pdp_logo),
                contentDescription = "Recent History Image",
                modifier = Modifier.size(100.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //date
                Text(
                    text = "April 21", style = TextStyle(
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.1.sp,
                        textAlign = TextAlign.Start
                    ), modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(25.dp)
                )

                Text(
                    text = "Late Blignt", style = TextStyle(
                        color = colorResource(R.color.font_color),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.1.sp,
                        textAlign = TextAlign.Start
                    ), modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(25.dp)
                )
            }

            IconButton(
                onClick = { /* Handle the button click */ },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "See More Icon",
                    tint = colorResource(R.color.font_color)
                )
            }
        }
    }
}

// bottom section
@Composable
fun HomeButtonContainer(
    initCameraActivity: () -> Unit, initAccountCenter: () -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
//            .systemBarsPadding()
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.White)
//            .background(colorResource(R.color.light_background))
    ) {
        //home
        IconButton(
            onClick = {
                val currentActivity = context as? Activity
                if (currentActivity !is HomeActivity) {
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.House,
                contentDescription = "Home Icon",
                tint = Color(4, 32, 129, 255),
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        // shutter button
        IconButton(
            onClick = { initCameraActivity() }) {
            Icon(
                imageVector = Icons.Default.CameraEnhance,
                contentDescription = "Camera Icon",
                tint = Color(4, 32, 129, 255),
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = {
                initAccountCenter()
            }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Camera Icon",
                tint = Color(4, 32, 129, 255),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeActivityUI() {
    PDPApplicationTheme {
        InitHomeActivityUI(
            initCameraActivity = { /* Dummy preview action */ },
            initAccountCenter = { /* Dummy preview action */ }
        )
    }
}
