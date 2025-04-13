package com.officialsunil.pdpapplication.viewui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.ProfileInformation

class AccountCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AccountCenterUI(
                    navigateToHome = { navigateToHome() }
                )
            }
        }
    }

    // function to go to the home activity
    fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

}


// composable function
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountCenterUI(navigateToHome : () -> Unit) {
    Scaffold(
        topBar = { AccountHeadingUI(navigateToHome) },
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
            AccountContainer()
        }
    }
}

@Composable
fun AccountHeadingUI(navigateToHome: () -> Unit) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(60.dp)
        ) {
            IconButton(
                onClick = {
                    navigateToHome()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Button ",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Account Center", style = TextStyle(
                    fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 1.2.sp
                ), textAlign = TextAlign.Start, modifier = Modifier.wrapContentSize()
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), thickness = 1.dp, color = Color.LightGray
        )
    }
}

@Composable
fun AccountContainer() {
    Column(
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        AccountInformationContainer()
    }
}

@Composable
fun AccountInformationContainer() {
    val profileInfo = getProfileInformation()
    if (profileInfo.isEmpty()) {

        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.warning),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(120.dp)
                .clip(shape = CircleShape)
        )

        Spacer(Modifier.height(50.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Looks like you've not Signed up, Signed up to track your profile",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        letterSpacing = 1.2.sp,
                        color = Color.Gray
                    )
                )

                OutlinedButton(
                    shape = RoundedCornerShape(16.dp), colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = colorResource(R.color.font_color),
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = Color.LightGray
                    ), onClick = {
                        Log.d("AccountCenterActivity", "Account Center Button Clicked")
                    },
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Sign Up Now"
                    )
                }
            }

        }
    } else {

        profileInfo.forEach { profile ->

            Spacer(Modifier.height(20.dp))

            Image(
                painter = painterResource(R.drawable.permission_rationale),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(150.dp)
                    .clip(shape = CircleShape)
                    .border(2.dp, shape = CircleShape, color = Color.Black)
            )

            Spacer(Modifier.height(50.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = profile.icon,
                        contentDescription = "Profile Icons",
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Spacer(Modifier.width(20.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = profile.title, textAlign = TextAlign.Start, style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.2.sp
                            )
                        )


                        Text(
                            text = profile.description,
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(5.dp), thickness = 1.dp, color = Color.LightGray
                )
            }
        }
    }
}

fun getProfileInformation(): List<ProfileInformation> {
    val currentUsersCredentials = FirebaseUserCredentials.getCurrentUserCredentails()

    return currentUsersCredentials?.let { user ->
        listOf(
            ProfileInformation(
                icon = Icons.Default.Person,
                title = "Name",
                description = user.name
            ),
            ProfileInformation(
                icon = Icons.Default.Info,
                title = "About",
                description = "Self Learned Developer"
            ),
            ProfileInformation(
                icon = Icons.Default.Phone,
                title = "Contact",
                description = user.phone
            ),
            ProfileInformation(
                icon = Icons.Default.Email,
                title = "Email",
                description = user.email
            )
        )
    } ?: emptyList()
}