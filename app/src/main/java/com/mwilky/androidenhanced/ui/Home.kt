package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.SystemClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mwilky.androidenhanced.BillingManager
import com.mwilky.androidenhanced.LogManager
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.Utils.Companion.BOOTTIME
import com.mwilky.androidenhanced.Utils.Companion.SHAREDPREFS
import com.mwilky.androidenhanced.Utils.Companion.UNSUPPORTEDDEVICEDIALOGSHOWN
import com.mwilky.androidenhanced.Utils.Companion.isDeviceSupported
import com.mwilky.androidenhanced.dataclasses.BottomNavigationItem
import com.mwilky.androidenhanced.dataclasses.TweaksCard
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    deviceProtectedStorageContext: Context
) {

    //Top App Bar
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        ScaffoldHomeCenteredAppBar(scrollBehavior = scrollBehavior)
    }, bottomBar = {
        ScaffoldNavigationBar(navController = navController)
    }, content = {
        HomeScreenScrollableContent(
            topPadding = it, bottomPadding = it, navController, deviceProtectedStorageContext = deviceProtectedStorageContext
        )
    })
}

@Composable
fun HomeScreenScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController,
    deviceProtectedStorageContext: Context
) {

    val sharedPreferences = deviceProtectedStorageContext.getSharedPreferences(SHAREDPREFS, Context.MODE_PRIVATE)
    val shouldShowUnsupportedDeviceDialog = rememberSaveable { mutableStateOf(false) }

    // Check current subscription status + whether we need to show unsupported device dialog
    LaunchedEffect(Unit) {

        if (!isDeviceSupported()) {

            val currentBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()
            val storedBootTime = sharedPreferences.getLong(BOOTTIME, -1L)
            val dialogShown = sharedPreferences.getBoolean(UNSUPPORTEDDEVICEDIALOGSHOWN, false)

            val toleranceAmount = 5 * 60 * 1000L

            if (storedBootTime == -1L || kotlin.math.abs(storedBootTime - currentBootTime) > toleranceAmount) {
                // Device has rebooted or first run
                sharedPreferences.edit()
                    .putLong(BOOTTIME, currentBootTime)
                    .putBoolean(UNSUPPORTEDDEVICEDIALOGSHOWN, false)
                    .apply()
                shouldShowUnsupportedDeviceDialog.value = true
            } else if (!dialogShown) {
                // Dialog has not been shown since last reboot
                shouldShowUnsupportedDeviceDialog.value = true
            }

        }
    }

    if (shouldShowUnsupportedDeviceDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Unsupported device!") },
            text = { Text("You are running this module on an unsupported device. All modifications have been disabled. Please join the telegram group for more information.") },
            confirmButton = {
                Button(onClick = {
                    shouldShowUnsupportedDeviceDialog.value = false
                    sharedPreferences.edit()
                        .putBoolean(UNSUPPORTEDDEVICEDIALOGSHOWN, true)
                        .apply()
                }) {
                    Text("OK")
                }
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = topPadding.calculateTopPadding(),
                bottom = bottomPadding.calculateBottomPadding()
            )
    ) {
        // Header Logo
        item(span = {
            GridItemSpan(2)
        }) {
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

        // Welcome Text
        item(span = {
            GridItemSpan(2)
        }) {
            //TODO: Detect username from google
            val greetingMessage: String by currentTimeFlow().collectAsState(
                initial = getGreetingMessage()
            )
            WelcomeText("Matt Wilkinson (mwilky)", greetingMessage)
        }

        item(span = {
            GridItemSpan(2)
        }) {
            AppVersion()
        }

        item(span = {
            GridItemSpan(2)
        }) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Statusbar",
                ), navController = navController
            )
        }
        item(span = {
            GridItemSpan(2)
        }) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Quicksettings",
                ), navController = navController
            )
        }
        item(span = {
            GridItemSpan(2)
        }) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Notifications",
                ), navController = navController
            )
        }
        item(span = {
            GridItemSpan(2)
        }) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Lockscreen",
                ), navController = navController
            )
        }
        item(span = {
            GridItemSpan(2)
        }) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Buttons",
                ), navController = navController
            )
        }
        item(span = {
            GridItemSpan(2)
        }) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Miscellaneous",
                ), navController = navController
            )
        }
        item(span = {
            GridItemSpan(2)
        }) {
            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

@Composable
fun WelcomeText(
    name: String, greetingMessage: String
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            //text = "$greetingMessage, $name",
            text = "$greetingMessage",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontFamily = caviarDreamsFamily
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TweaksItem(
    card: TweaksCard, navController: NavController
) {
    ElevatedCard(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize()
        .clickable(enabled = true, onClick = {
            navController.navigate(Screens.Tweaks.withArgs(card.label))
        }), shape = RoundedCornerShape(10.dp), colors = CardDefaults.elevatedCardColors()) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
            //verticalArrangement = Arrangement.spacedBy(10.dp)
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
                //OutlinedButton(onClick = { /*TODO*/ }) { Text(text = "Reset to defaults") }
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                //OutlinedButton(onClick = { /*TODO*/ }) { Text(text = "Enter") }
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
fun currentTimeFlow(): Flow<String> = flow {
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

fun getGreetingMessage(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning!"
        in 12..16 -> "Good afternoon!"
        else -> "Good evening!"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldHomeCenteredAppBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        ),
        title = {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp
                        )
                    ) {
                        append("Android")
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

    var pageText = screen

    when (screen) {

        "Individual statusbar icon colors" -> {
            pageText = "Individual icon colors"
        }

        "Individual quicksettings statusbar icon colors" -> {
            pageText = "Individual icon colors"
        }

        "Individual lockscreen statusbar icon colors" -> {
            pageText = "Individual icon colors"
        }

    }

    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
    ), scrollBehavior = scrollBehavior, title = {
        Text(
            text = pageText,
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
            Text(text = "Version: ", fontFamily = caviarDreamsFamily)
            Text(text = appVersion, fontFamily = caviarDreamsFamily)
        }
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/AndroidEnhanced"))
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


@Composable
fun ScaffoldNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navBarItemsList = listOf(
        BottomNavigationItem(
            title = "Logs",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            route = "logs"
        ), BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home"
        ), BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = "settings"
        )
    )
    NavigationBar {
        navBarItemsList.forEach { bottomNavigationItem ->
            NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == bottomNavigationItem.route } == true,
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
                },
                colors = NavigationBarItemDefaults.colors(),
                alwaysShowLabel = false
            )
        }
    }
}
