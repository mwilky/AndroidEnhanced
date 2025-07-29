package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mwilky.androidenhanced.BuildConfig
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.BOOTTIME
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEDIALOGSHOWN
import com.mwilky.androidenhanced.Utils.Companion.isDeviceSupported
import com.mwilky.androidenhanced.dataclasses.BottomNavigationItem
import com.mwilky.androidenhanced.dataclasses.Screens
import com.mwilky.androidenhanced.dataclasses.TweaksCard
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController, deviceProtectedStorageContext: Context
) {
    //Top App Bar
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldHomeCenteredAppBar(scrollBehavior = scrollBehavior)
    }, bottomBar = {
        ScaffoldNavigationBar(navController = navController)
    }, content = {
        HomeScreenScrollableContent(
            topPadding = it,
            bottomPadding = it,
            navController,
            deviceProtectedStorageContext = deviceProtectedStorageContext
        )
    })
}

// list of tw
private val tweakItemIds = listOf(
    R.string.statusbar,
    R.string.quicksettings,
    R.string.notifications,
    R.string.lockscreen,
    R.string.buttons,
    R.string.miscellaneous
)

@Composable
fun HomeScreenScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController,
    deviceProtectedStorageContext: Context
) {
    val sharedPreferences =
        deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, Context.MODE_PRIVATE)
    val shouldShowUnsupportedDeviceDialog = checkUnsupportedDeviceDialog(sharedPreferences)

    if (shouldShowUnsupportedDeviceDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Unsupported device!") },
            text = { Text("You are running this module on an unsupported device. All modifications have been disabled. Please join the telegram group for more information.") },
            confirmButton = {
                Button(onClick = {
                    shouldShowUnsupportedDeviceDialog.value = false
                    sharedPreferences.edit {
                        putBoolean(UNSUPPORTEDDEVICEDIALOGSHOWN, true)
                    }
                }) {
                    Text("OK")
                }
            })
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding.calculateTopPadding(),
                bottom = bottomPadding.calculateBottomPadding()
            ),
    ) {

        item(key = "header") {
            // Header Logo
            Image(
                painter = painterResource(id = R.drawable.header_logo),
                contentDescription = "Image Description",
                alignment = Alignment.Center,
                modifier = Modifier.padding(
                    start = 64.dp, end = 64.dp, top = 16.dp, bottom = 16.dp
                ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcIn),
            )
        }

        item(key = "greetings") {
            val greetingMessage: String by currentTimeFlow(deviceProtectedStorageContext).collectAsState(
                initial = getGreetingMessage(deviceProtectedStorageContext)
            )
            WelcomeText(greetingMessage)
        }

        item(key = "app_version") { AppVersion() }

        if (!BuildConfig.HAS_PREMIUM_MODULE) {
            item(key = "source_built") {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(32.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                        text = "Android Enhanced has been built from source, premium features will be missing.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                        fontFamily = caviarDreamsFamily
                    )
                }
            }
        }

        items(tweakItemIds, key = { it }) { resId ->
            val label = stringResource(id = resId)
            val card = TweaksCard(Icons.Outlined.Build, label)
            TweaksItem(card = card, navController = navController)
        }

        item(key = "spacer") {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun WelcomeText(
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
            text = greetingMessage,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontFamily = caviarDreamsFamily
        )
    }
}

@Composable
fun TweaksItem(
    card: TweaksCard, navController: NavController
) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(enabled = true, onClick = {
                navController.navigate(card.label.toLowerCase(Locale.current))
            }), colors = CardDefaults.elevatedCardColors()
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = card.label,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                fontFamily = caviarDreamsFamily
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun currentTimeFlow(context: Context): Flow<String> = flow {
    val calendar = Calendar.getInstance()
    var currentHour = calendar.get(Calendar.HOUR_OF_DAY)

    while (true) {
        delay(60000)
        calendar.timeInMillis = System.currentTimeMillis()
        val newHour = calendar.get(Calendar.HOUR_OF_DAY)

        if (newHour != currentHour) {
            currentHour = newHour
            val greetingMessage = getGreetingMessage(context)
            emit(greetingMessage)
        }
    }
}

fun getGreetingMessage(context: Context): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> context.getString(R.string.goodMorning)
        in 12..16 -> context.getString(R.string.goodAfternoon)
        else -> context.getString(R.string.goodEvening)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldHomeCenteredAppBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
        ), title = {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp
                        )
                    ) {
                        append(stringResource(R.string.android))
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = (-1).sp
                        )
                    ) {
                        append(stringResource(R.string.enhanced))
                    }
                }, fontFamily = caviarDreamsFamily
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldTweaksAppBar(
    navController: NavController,
    screen: String,
    showBackIcon: Boolean,
    scrollBehavior: TopAppBarScrollBehavior
) {

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
    ), scrollBehavior = scrollBehavior, title = {
        Text(
            text = screen,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = caviarDreamsFamily
        )
    }, navigationIcon = {
        if (showBackIcon) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back"
                )
            }
        }
    })
}

@Composable
fun AppVersion() {
    val context = LocalContext.current
    val appVersion = remember { getAppVersion(context) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 32.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = "${stringResource(R.string.version)} ", fontFamily = caviarDreamsFamily
            )
            Text(text = appVersion, fontFamily = caviarDreamsFamily)
        }
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, "https://t.me/AndroidEnhanced".toUri())
            context.startActivity(intent)
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_telegram),
                contentDescription = "Telegram",
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurface, BlendMode.SrcIn
                ),
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navBarItemsList = listOf(
        BottomNavigationItem(
            title = stringResource(R.string.logs),
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            route = "logs"
        ), BottomNavigationItem(
            title = stringResource(R.string.home),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home"
        ), BottomNavigationItem(
            title = stringResource(R.string.settings),
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = "settings"
        )
    )

    NavigationBar {
        navBarItemsList.forEach { bottomNavigationItem ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == bottomNavigationItem.route } == true,
                onClick = {
                    navController.navigate(bottomNavigationItem.route) {
                        // Pop up to the home screen of the app to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(Screens.Home.route) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = if (currentDestination?.hierarchy?.any { it.route == bottomNavigationItem.route } == true) bottomNavigationItem.selectedIcon else bottomNavigationItem.unselectedIcon,
                        contentDescription = bottomNavigationItem.title)
                },
                label = {
                    Text(
                        text = bottomNavigationItem.title,
                        fontFamily = caviarDreamsFamily,
                        fontWeight = FontWeight.Bold
                    )
                })
        }
    }
}

@Composable
fun checkUnsupportedDeviceDialog(
    sharedPreferences: SharedPreferences
): MutableState<Boolean> {
    val showDialog = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isDeviceSupported()) {
            val currentBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()
            val storedBootTime = sharedPreferences.getLong(BOOTTIME, -1L)
            val dialogShown = sharedPreferences.getBoolean(UNSUPPORTEDDEVICEDIALOGSHOWN, false)
            val tolerance = 5 * 60 * 1000L

            if (storedBootTime == -1L || abs(storedBootTime - currentBootTime) > tolerance) {
                sharedPreferences.edit {
                    putLong(BOOTTIME, currentBootTime)
                    putBoolean(UNSUPPORTEDDEVICEDIALOGSHOWN, false)
                }
                showDialog.value = true
            } else if (!dialogShown) {
                showDialog.value = true
            }
        }
    }

    return showDialog
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()

    HomeScreen(
        navController = navController,
        deviceProtectedStorageContext = context
    )
}



