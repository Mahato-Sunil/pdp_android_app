package com.officialsunil.pdpapplication

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.light_background))
            .padding(start = 5.dp, end = 5.dp)
            .systemBarsPadding()
    ) {
        HomeHeadingUI()
        HomeContainer()
        HomeButtonContainer(initCameraActivity)
    }
}


/* ========================================================================
    UI Layout and Composable functions
   ========================================================================
 */
@Composable
fun HomeHeadingUI() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(180.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Image(
            painter = painterResource(R.drawable.pdp_logo_text),
            contentDescription = "System Logo",
            modifier = Modifier
                .width(120.dp)
                .padding(start = 10.dp, 0.dp)
        )

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Icon",
            tint = colorResource(R.color.font_color),
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )
    }
}

@Composable
fun HomeContainer() {
    Spacer(modifier = Modifier.height(40.dp))

    // card
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.light_card_background),
            contentColor = colorResource(R.color.font_color)
        ), modifier = Modifier
            .fillMaxWidth(fraction = 0.9f)
            .height(225.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(colorResource(R.color.extra_light_card_background)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tips of the Day",
                style = TextStyle(
                    color = colorResource(R.color.font_color),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                ),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Store potatoes in a cool, dark, and well-ventilated place, away from onions, to prevent sprouting and decay.",
                style = TextStyle(
                    color = colorResource(R.color.font_color),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = TextUnit(1.5f, TextUnitType.Sp),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(10.dp)

            )
        }
    }

    // history section
    Spacer(modifier = Modifier.height(40.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.extra_light_card_background)
            ), modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 10.dp, 2.dp)
                    .height(35.dp)
            ) {
                Text(
                    text = "Recent Scanned Diseases",
                    style = TextStyle(
                        color = colorResource(R.color.font_color),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                    ),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(2.dp)
                        .background(colorResource(R.color.dark_card_background))
                        .fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth(.45f)
                            .height(100.dp)
                            .background(colorResource(R.color.extra_light_card_background))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pdp_logo),
                            contentDescription = "Recent History Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    OutlinedButton(
                        onClick = {}, modifier = Modifier.fillMaxWidth(.8f),

                        border = BorderStroke(
                            2.dp, color = colorResource(R.color.light_card_background)
                        ), shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            text = "See More", style = TextStyle(
                                color = colorResource(R.color.light_background),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ), modifier = Modifier.wrapContentSize()
                        )

                        Spacer(Modifier.width(15.dp))
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "See More Icon",
                            tint = colorResource(R.color.light_background)
                        )
                    }
                }
            }

        }
    }
}

// bottom section
@Composable
fun HomeButtonContainer(initCameraActivity: () -> Unit) {
    //declare the paths and variables
    val barHeight = 48.dp
    val fabRadius = 28.dp
    val background = colorResource(R.color.light_card_background)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Draw the bottom bar background with curved cutout
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + fabRadius / 2f)
                .align(Alignment.BottomCenter)
        ) {
            //  size denotes the size of the canvax in canvas scope
            val width = size.width
            val height = size.height
            val fabDiameter = fabRadius.toPx() * 2
            val fabMargin = 50.dp.toPx()

            val curveDepth = fabRadius.toPx() * 1.57f   // for flattening the craddle depth

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width / 2f - fabDiameter / 2f - fabMargin, 0f)

                cubicTo(
                    width / 2f - fabDiameter / 2f,
                    0f,
                    width / 2f - fabRadius.toPx(),
                    curveDepth,
                    width / 2f,
                    curveDepth
                )

                cubicTo(
                    width / 2f + fabRadius.toPx(),
                    curveDepth,
                    width / 2f + fabDiameter / 2f,
                    0f,
                    width / 2f + fabDiameter / 2f + fabMargin,
                    0f
                )

                lineTo(width, 0f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = path, color = background, style = Fill
            )
        }

        // Floating button content
        Box(
            modifier = Modifier
                .size(fabRadius * 2f)
                .aspectRatio(1f)
                .align(Alignment.BottomCenter)
                .offset(y = -(fabRadius * 0.8f))
                .clip(CircleShape)
                .border(1.6.dp, colorResource(R.color.dark_card_background), CircleShape),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.camera_shutter),
                contentDescription = "Capture Button",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        initCameraActivity()
                    })
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewUI() {
    InitHomeActivityUI(
        initCameraActivity = { })
}

