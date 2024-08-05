package com.mwilky.androidenhanced.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils.Companion.ISONBOARDINGCOMPLETEDKEY

@Composable
fun OnboardingScreen(navController: NavController, context: Context) {
    val deviceProtectedStorageContext: Context = context.createDeviceProtectedStorageContext()
    val sharedPreferences: SharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(
            BroadcastUtils.PREFS, Context.MODE_PRIVATE
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Onboarding Content
        //Main Text
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = (-1).sp),
                ) {
                    append("Android ")
                }

                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    fontSize = 32.sp,
                    letterSpacing = (-1).sp,
                )
                ) {
                    append("Enhanced")
                }
            },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(16.dp)
        )
        //Secondary Text
        Text(
            text = "Welcome to Android Enhanced! This app heavily relies on Xposed framework. " +
                    "Functionality will be limited without it...",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
        )
        //Continue button
        Button(
            onClick = {
                //Set onboarding complete
                sharedPreferences.edit().putBoolean(ISONBOARDINGCOMPLETEDKEY, true).apply()
                //Go to Home-screen
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Onboarding.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Continue",
                color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}