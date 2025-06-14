package com.officialsunil.pdpapplication.viewui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.SocialMediaIcon
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme
import androidx.core.net.toUri
import com.officialsunil.pdpapplication.utils.NavigationUtils

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AboutActivityUI(
                    navigateTo = { url -> navigateTo(url) })
            }
        }
    }

    fun navigateTo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AboutActivityUI(navigateTo: (String) -> Unit) {
    Scaffold(
        topBar = { AboutHeaderUI() },
        modifier = Modifier
            .systemBarsPadding()
            .background(colorResource(R.color.light_background))
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            AboutContainerUI(navigateTo)
        }
    }
}

@Composable
fun AboutHeaderUI() {
    val context = LocalContext.current
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(60.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "accountCenter", true)
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Arrow Back",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "About Developer", style = TextStyle(
                    fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 1.2.sp
                ), textAlign = TextAlign.Start, modifier = Modifier.wrapContentSize()
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
fun AboutContainerUI(navigateTo: (String) -> Unit) {
    val photoUrl =
        "https://mahatosunil.com.np/profile.png"

    Log.d("User Photo", photoUrl)
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(200.dp) // Same size as your image
                .padding(8.dp)
        ) {
            // Profile image
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                error = painterResource(R.drawable.image),
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .zIndex(1f)
                    .border(2.dp, color = Color.Black, shape = CircleShape)
            )

            Image(
                painter = painterResource(R.drawable.verified_tick),
                contentDescription = "Verified",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = (-4).dp)
                    .zIndex(1f),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(50.dp))
        // other ui
        Text(
            text = "Sunil Mahato", style = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 1.2.sp
            ), textAlign = TextAlign.Center, modifier = Modifier.wrapContentSize()
        )

        Spacer(Modifier.height(30.dp))
        Text(
            text = "Portfolio",
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                letterSpacing = 1.2.sp,
                textDecoration = TextDecoration.Underline
            ),
            color = Color.Blue,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentSize()
                .clickable {
                    navigateTo("https://mahatosunil.com.np/")
                })


        Spacer(Modifier.height(20.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        Spacer(Modifier.height(35.dp))

        Text(
            text = "Let's Connect",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 1.2.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.wrapContentSize(),
            textDecoration = TextDecoration.Underline
        )

        // icons to social media

        Spacer(Modifier.height(40.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            val socialMediaIcon = getSocialMediaIcon()
            socialMediaIcon.forEach { icons ->
                IconButton(
                    onClick = {
                        navigateTo(icons.profileUrl)
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(icons.icon)
                            .crossfade(true).placeholder(R.drawable.no_picture)
                            .error(R.drawable.no_picture).build(),
                        contentDescription = icons.description,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

fun getSocialMediaIcon(): List<SocialMediaIcon> {
    return listOf(
        SocialMediaIcon(
            icon = "https://upload.wikimedia.org/wikipedia/en/thumb/0/04/Facebook_f_logo_%282021%29.svg/2048px-Facebook_f_logo_%282021%29.svg.png",
            description = "Facebook Logo ",
            profileUrl = "https://www.facebook.com/officialSpinyBabbler"
        ),

        SocialMediaIcon(
            icon = "https://store-images.s-microsoft.com/image/apps.43327.13510798887167234.cadff69d-8229-427b-a7da-21dbaf80bd81.79b8f512-1b22-45d6-9495-881485e3a87e",
            description = "Instagram Icon",
            profileUrl = "https://www.instagram.com/official_sunilmahato"
        ),

        SocialMediaIcon(
            icon = "https://scontent.fktm6-1.fna.fbcdn.net/v/t39.8562-6/480940731_1834248350688384_1480736336186282972_n.png?_nc_cat=110&ccb=1-7&_nc_sid=f537c7&_nc_ohc=HifC6u_XNZoQ7kNvwFSiHSR&_nc_oc=AdnqgAVE-AtnaOGc2LtxWoV2uKzw7fSBxB0rEsdiePdPTFsP3ha6UGI8TIBU_CFrXOg&_nc_zt=14&_nc_ht=scontent.fktm6-1.fna&_nc_gid=3FdK4deO_9ytfO4PLI2hwQ&oh=00_AfE-Mg29VGCIQQbKXvwmgS8cM_RV_4gOOuuxbvysxEn3uA&oe=6804FE98",
            description = "Whatsapp Icon",
            profileUrl = "https://wa.me/qr/AZF7XZW4PIHNA1"
        ),

        SocialMediaIcon(
            icon = "https://github.githubassets.com/assets/GitHub-Mark-ea2971cee799.png",
            description = "Github Icon",
            profileUrl = "https://github.com/Mahato-Sunil"
        ),

        SocialMediaIcon(
            icon = "https://content.linkedin.com/content/dam/me/business/en-us/amp/xbu/linkedin-revised-brand-guidelines/in-logo/fg/brand-inlogo-hero-fg-dsk-v01.png/jcr:content/renditions/brand-inlogo-hero-fg-dsk-v01-2x.png",
            description = "LinkedIn Icon",
            profileUrl = "https://www.linkedin.com/in/sunil-mahato-3178892a9/"
        )
    )
}