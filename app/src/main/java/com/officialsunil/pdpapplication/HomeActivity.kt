package com.officialsunil.pdpapplication

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {

            }
        }
    }
}

//  activity layout
@Composable
fun InitHomeActivityUI(){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.light_background))
            .padding(start = 5.dp, end = 5.dp)
            .systemBarsPadding()
    ) {
        HomeHeadingUI()
    }
}

// heading section
@Composable
fun HomeHeadingUI(){
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewUI(){
    InitHomeActivityUI()
}


