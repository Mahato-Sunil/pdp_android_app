package com.officialsunil.pdpapplication.viewui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
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
import androidx.compose.ui.zIndex
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                InitHomeActivityUI(
                    initCameraActivity = { navigateToCameraActivity() })
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
}

//  activity layout
@Composable
fun InitHomeActivityUI(initCameraActivity: () -> Unit) {
    Scaffold(
        topBar = { HomeHeadingUI() },
        bottomBar = { HomeButtonContainer(initCameraActivity) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .systemBarsPadding() // 👈 Add system bar padding first
                .padding(innerPadding) // 👈 Then scaffold inner padding
                .fillMaxSize()
                .background(colorResource(R.color.light_background))
                .verticalScroll(rememberScrollState())
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
        modifier = Modifier.fillMaxWidth().systemBarsPadding()
    ) {
        Image(
            painter = painterResource(R.drawable.pdp_logo_text),
            contentDescription = "System Logo",
            modifier = Modifier
                .width(100.dp)
                .padding(start = 16.dp),
            contentScale = ContentScale.Fit
        )
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            color = Color.LightGray
        )
    }
}

@Composable
fun HomeContainer() {
    val localContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth().systemBarsPadding()
    ) {
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
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
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "See More Icon",
                    tint = colorResource(R.color.font_color)
                )
            }
        }
    }
}

// bottom section
@Composable
fun HomeButtonContainer(initCameraActivity: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(colorResource(R.color.extra_light_card_background))
    ) {
        //home
        IconButton(
            onClick = { /* Handle the button click */ },
        ) {
            Icon(
                imageVector = Icons.Default.House,
                contentDescription = "Home Icon",
                tint = colorResource(R.color.font_color),
                modifier = Modifier.fillMaxSize()
            )
        }

        // shutter button
        IconButton(
            onClick = { initCameraActivity() }) {
            Icon(
                imageVector = Icons.Default.CameraEnhance,
                contentDescription = "Camera Icon",
                tint = colorResource(R.color.font_color),
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = { initCameraActivity() }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Camera Icon",
                tint = colorResource(R.color.font_color),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewUI() {
    InitHomeActivityUI(
        initCameraActivity = { })
}

