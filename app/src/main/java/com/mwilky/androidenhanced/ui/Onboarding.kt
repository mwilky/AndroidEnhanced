package com.mwilky.androidenhanced.ui

import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mwilky.androidenhanced.DataStoreManager
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private lateinit var dataStore: DataStoreManager

@Composable
fun OnboardingScreen(navController: NavController, context: Context) {
    dataStore = DataStoreManager(context)
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
            modifier = Modifier
                .padding(16.dp)
        )
        //Secondary Text
        Text(
            text = "Welcome to Android Enhanced! This app heavily relies on Xposed framework. " +
                    "Functionality will be limited without it...",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(16.dp)
        )
        //Continue button
        Button(
            onClick = {
                //Set onboarding complete
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.dataStore.edit { preferences ->
                        preferences[DataStoreManager.onboardingCompletedKey] = true
                    }
                }
                //Go to Home-screen
                navController.navigate(Screen.Home.route)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    AndroidEnhancedTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        OnboardingScreen(navController, context)
    }
}

