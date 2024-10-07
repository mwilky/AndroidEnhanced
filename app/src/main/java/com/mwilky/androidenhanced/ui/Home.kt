package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PaintDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mwilky.androidenhanced.BillingManager
import com.mwilky.androidenhanced.R
import com.mwilky.androidenhanced.dataclasses.BottomNavigationItem
import com.mwilky.androidenhanced.dataclasses.EnvironmentProp
import com.mwilky.androidenhanced.dataclasses.TweaksCard
import com.mwilky.androidenhanced.ui.theme.caviarDreamsFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, deviceProtectedStorageContext: Context, billingManager: BillingManager) {

    //Top App Bar
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScaffoldHomeCenteredAppBar(scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            ScaffoldNavigationBar(navController = navController)
        },
        content = {
            HomeScreenScrollableContent(topPadding = it, bottomPadding = it, navController, billingManager = billingManager)
        }
    )
}

@Composable
fun HomeScreenScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController,
    billingManager: BillingManager
) {
    // Check current subscription status
    LaunchedEffect(Unit) {
        billingManager.checkSubscriptionStatus(true)
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
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.header_logo),
                contentDescription = "Image Description",
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(
                        start = 64.dp,
                        end = 64.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary, BlendMode.SrcIn),
            )
        }

        // Welcome Text
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            //TODO: Detect username from google
            val greetingMessage: String by currentTimeFlow().collectAsState(
                initial = getGreetingMessage()
            )
            WelcomeText("Matt Wilkinson (mwilky)", greetingMessage)
        }

        // Environment First Row
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            //TODO: detect if xposed and module is configured + if is premium
            EnvironmentRow(
                props = listOf(
                    EnvironmentProp(
                        icon = Icons.Outlined.Build,
                        label = "Module:",
                        value = "Loaded"
                    ),
                    EnvironmentProp(
                        icon = Icons.Outlined.Create,
                        label = "Premium:",
                        value = /*if (isPremiumFeatures) "Unlocked" else*/ "Locked"
                    )
                )
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Statusbar",
                ),
                navController = navController
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Quicksettings",
                ),
                navController = navController
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Notifications",
                ),
                navController = navController
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Lockscreen",
                ),
                navController = navController
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Buttons",
                ),
                navController = navController
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Miscellaneous",
                ),
                navController = navController
            )
        }
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )
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
            //text = "$greetingMessage, $name",
            text = "$greetingMessage",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontFamily = caviarDreamsFamily
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EnvironmentRow(
    props: List<EnvironmentProp>
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        props.forEach { environmentProp ->
            CircularImageButton(
                prop = environmentProp,
            )
        }
    }
}

@Composable
fun CircularImageButton(
    prop: EnvironmentProp,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
    ) {
        Icon(
            imageVector = prop.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(56.dp)
                .border(
                    width = Dp.Hairline,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                )
                .padding(12.dp)
        )
        Text(
            text = prop.label,
            lineHeight = 16.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = prop.value,
            lineHeight = 16.sp,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TweaksItem(
    card: TweaksCard, navController: NavController
) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .clickable(
                enabled = true,
                onClick = {
                    navController.navigate(Screens.Tweaks.withArgs(card.label))
                }
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
            //verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = card.label,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(start = 4.dp, top = 8.dp),
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
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        ),
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
                },
                fontFamily = caviarDreamsFamily
            )
        }
    )
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

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
        ),
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = pageText,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = caviarDreamsFamily
            )
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        }
    )
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
        ),
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home"
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = "settings"
        )
    )
    NavigationBar {
        navBarItemsList.forEach { bottomNavigationItem ->
            NavigationBarItem(
                selected =
                currentDestination?.hierarchy?.any { it.route == bottomNavigationItem.route }
                        == true,
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
                    Icon(
                        imageVector =
                        if (currentDestination?.hierarchy?.any
                            { it.route == bottomNavigationItem.route } == true
                        )
                            bottomNavigationItem.selectedIcon else
                            bottomNavigationItem.unselectedIcon,
                        contentDescription = bottomNavigationItem.title
                    )
                },
                label = {
                    Text(text = bottomNavigationItem.title)
                },
                colors = NavigationBarItemDefaults.colors(),
                alwaysShowLabel = false
            )
        }
    }
}
