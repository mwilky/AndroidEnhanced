package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mwilky.androidenhanced.DataStoreManager
import com.mwilky.androidenhanced.dataclasses.EnvironmentProp
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, context: Context) {
    AndroidEnhancedTheme {
        // Pressing back on Homescreen should close the app, rather than going back to onboarding
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HandleBackButton {
                (context as? Activity)?.finish()
            }
            //Top App Bar
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    CenterAlignedTopAppBar(
                        scrollBehavior = scrollBehavior,
                        title = {
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-1).sp
                                        )
                                    ) {
                                        append("Android ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Light,
                                            fontStyle = FontStyle.Italic,
                                            letterSpacing = (-1).sp
                                        )
                                    ) {
                                        append("Enhanced")
                                    }
                                }, lineHeight = 64.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { /* doSomething() */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    )
                },
                content = {
                    Menu(topPadding = it)
                }
            )
        }
    }
}

@Composable
fun Menu(topPadding: PaddingValues, ) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    // Collect the isDeviceSupported state from the DataStore
    val isDeviceSupported by dataStoreManager.isDeviceSupportedFlow.collectAsState(initial = false)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding.calculateTopPadding(), start = 16.dp, end = 16.dp)
    ) {
        Column {
            val greetingMessage: String by currentTimeFlow().collectAsState(
                initial = getGreetingMessage()
            )
            //TODO: Setup with datastore, hardcoded for now
            val isPremiumFeatures = true
            WelcomeText("Matt Wilkinson (mwilky)", greetingMessage)
            //TODO: temporary icons
            EnvironmentSection(props = listOf(
                EnvironmentProp(
                    icon = Icons.Outlined.Phone,
                    label = "Device:",
                    value = Build.MODEL
                ),
                EnvironmentProp(
                    icon = Icons.Outlined.Lock,
                    label = "Security patch:",
                    value = Build.VERSION.SECURITY_PATCH
                ),
                EnvironmentProp(
                    icon = Icons.Outlined.CheckCircle,
                    label = "Device support:",
                    value = if (isDeviceSupported) "Supported" else "Not supported"
                ),
                EnvironmentProp(
                    icon = Icons.Outlined.Create,
                    label = "Premium features:",
                    value = if (isPremiumFeatures) "Unlocked" else "Locked"
                )
            ))
        }
    }
}

@Composable
fun HandleBackButton(onBackPressed: () -> Unit) {
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backCallback = rememberUpdatedState(onBackPressed)

    DisposableEffect(backCallback.value) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backCallback.value()
            }
        }

        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
}

@Composable
fun WelcomeText(
    name: String,
    greetingMessage: String
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "$greetingMessage, $name",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EnvironmentSection(
    props : List<EnvironmentProp>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(props.size) {
            Column(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EnvironmentItem(props[it])
            }
        }
    }
}

@Composable
fun EnvironmentItem(
    prop: EnvironmentProp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = prop.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(32.dp)
        )
        Text(
            text = prop.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = prop.value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun currentTimeFlow(): Flow<String> = flow {
    val calendar = Calendar.getInstance()
    var currentHour = calendar.get(Calendar.HOUR_OF_DAY)

    while (true) {
        delay(60000) // Wait for a minute
        calendar.timeInMillis = System.currentTimeMillis()
        val newHour = calendar.get(Calendar.HOUR_OF_DAY)

        if (newHour != currentHour) {
            currentHour = newHour
            val greetingMessage = getGreetingMessage()
            emit(greetingMessage)
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun HomeScreenPreview() {
    AndroidEnhancedTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        HomeScreen(navController, context)
    }
}

private fun getGreetingMessage(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
