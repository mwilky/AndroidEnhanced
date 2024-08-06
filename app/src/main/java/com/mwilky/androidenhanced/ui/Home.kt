package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import com.mwilky.androidenhanced.dataclasses.BottomNavigationItem
import com.mwilky.androidenhanced.dataclasses.EnvironmentProp
import com.mwilky.androidenhanced.dataclasses.TweaksCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, deviceProtectedStorageContext: Context) {

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
            HomeScreenScrollableContent(topPadding = it, bottomPadding = it, navController)
        }
    )
}

@Composable
fun HomeScreenScrollableContent(
    topPadding: PaddingValues,
    bottomPadding: PaddingValues,
    navController: NavController
) {
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
                imageVector = headerImage(),
                contentDescription = "Image Description",
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(
                        start = 64.dp,
                        end = 64.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
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
        )  {
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
                .padding(start =  16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
            //verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = card.label,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(start = 4.dp, top = 8.dp)
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
                    tint =  MaterialTheme.colorScheme.primary
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
@Composable
fun headerImage(
    defaultWidth: Dp = 688.2.dp,
    defaultHeight: Dp = 263.12.dp,
    viewportWidth: Float = 688.2f,
    viewportHeight: Float = 263.12f,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onSurfaceColor: Color = MaterialTheme.colorScheme.onSurface,
): ImageVector {

    return ImageVector.Builder(
        defaultWidth = defaultWidth,
        defaultHeight = defaultHeight,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
    ).run {
        addPath(
            pathData = addPathNodes("m133.52,76.5h-41.39l-23.66,63.8h-7.17L112.83," +
                    "0l44.45,122.3h-7.1l-16.66,-45.8ZM131.05,69.78l-18.23,-50.13 -18.23," +
                    "50.13h36.46Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m178.95,65.29v57h-6.38l-2,-73.36h8.38v4.71c6.13," +
                    "-6.08 13.25,-9.11 21.37,-9.11 8.67,0 15.71,2.72 21.14,8.14 5.43,5.43 8.22," +
                    "12.28 8.37,20.54v49.08h-6.72v-48.48c0,-6.23 -2.25,-11.54 -6.76,-15.95 -4.51," +
                    "-4.41 -9.85,-6.61 -16.02,-6.61s-11.5,2.17 -15.99,6.5c-2.44,2.44 -4.23," +
                    "4.96 -5.38,7.55Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m331.42,122.3h-6.72v-14.94c-1.3,1.74 -2.72,3.41 -4.26," +
                    "5.01 -8.02,8.02 -17.71,12.03 -29.06,12.03s-20.97,-4.01 -28.99,-12.03c-8.02," +
                    "-8.02 -12.03,-17.68 -12.03,-28.99s4.01,-21.04 12.03,-29.06 17.68," +
                    "-12.03 28.99,-12.03 21.04,4.01 29.06,12.03c1.54,1.59 2.96,3.26 4.26," +
                    "5.01L324.7,2.76h6.72v119.53ZM324.7,90.03v-13.3c-1.25,-6.57 -4.36," +
                    "-12.35 -9.34,-17.33 -6.62,-6.62 -14.62,-9.94 -23.98,-9.94s-17.36," +
                    "3.31 -23.98,9.94 -9.94,14.62 -9.94,23.98 3.31,17.34 9.94,23.94c6.62," +
                    "6.6 14.62,9.9 23.98,9.9s17.36,-3.31 23.98,-9.94c4.98,-4.98 8.09," +
                    "-10.73 9.34,-17.26Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m353.83,48.93h6.72v13.37c1.39,-1.99 3.01,-3.91 4.86," +
                    "-5.75 5.93,-5.93 12,-9.04 18.23,-9.34v7.32c-4.23,0.2 -8.62,2.57 -13.15," +
                    "7.1 -6.38,6.33 -9.69,13.92 -9.94,22.79v37.88h-6.72V48.93Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m433.62,42.28c11.36,0 21.03,4.01 29.02,12.03 7.99," +
                    "8.02 11.99,17.71 11.99,29.06s-4,20.97 -11.99,28.99c-7.99,8.02 -17.67," +
                    "12.03 -29.02,12.03s-20.97,-4.01 -28.99,-12.03c-8.02,-8.02 -12.03," +
                    "-17.68 -12.03,-28.99s4.01,-21.04 12.03,-29.06 17.68,-12.03 28.99," +
                    "-12.03ZM409.64,59.39c-6.62,6.62 -9.94,14.62 -9.94,23.98s3.31,17.34 9.94," +
                    "23.94c6.62,6.6 14.62,9.9 23.98,9.9s17.36,-3.3 23.98,-9.9c6.62,-6.6 9.94," +
                    "-14.58 9.94,-23.94s-3.31,-17.36 -9.94,-23.98c-6.62,-6.62 -14.62," +
                    "-9.94 -23.98,-9.94s-17.36,3.31 -23.98,9.94Z"),
            fill = SolidColor(primaryColor)
        )
        addPath(
            pathData = addPathNodes("m502.17,24.88c1.32,1.3 1.98,2.86 1.98,4.71s-0.66," +
                    "3.49 -1.98,4.78c-1.32,1.3 -2.9,1.94 -4.74,1.94s-3.43,-0.65 -4.74,-1.94c-1.32," +
                    "-1.29 -1.98,-2.89 -1.98,-4.78s0.66,-3.41 1.98,-4.71c1.32,-1.29 2.9," +
                    "-1.94 4.74,-1.94s3.42,0.65 4.74,1.94ZM500.79,48.94v73.36h-6.72L494.07," +
                    "48.93h6.72Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m603.88,122.3h-6.72v-14.94c-1.3,1.74 -2.71," +
                    "3.41 -4.26,5.01 -8.02,8.02 -17.71,12.03 -29.06,12.03s-20.97,-4.01 -28.99," +
                    "-12.03c-8.02,-8.02 -12.03,-17.68 -12.03,-28.99s4.01,-21.04 12.03," +
                    "-29.06c8.02,-8.02 17.68,-12.03 28.99,-12.03s21.04,4.01 29.06,12.03c1.54," +
                    "1.59 2.96,3.26 4.26,5.01L597.16,2.76h6.72v119.53ZM597.16,90.03v-13.3c-1.25," +
                    "-6.57 -4.36,-12.35 -9.34,-17.33 -6.62,-6.62 -14.62,-9.94 -23.98," +
                    "-9.94s-17.36,3.31 -23.98,9.94c-6.62,6.62 -9.94,14.62 -9.94,23.98s3.31," +
                    "17.34 9.94,23.94c6.62,6.6 14.62,9.9 23.98,9.9s17.36,-3.31 23.98,-9.94c4.98," +
                    "-4.98 8.09,-10.73 9.34,-17.26Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m18.45,244.6c6.32,5.78 13.85,8.67 22.56,8.67 9.36," +
                    "0 17.36,-3.31 23.98,-9.94 0.85,-0.85 1.64,-1.72 2.39,-2.61l7.1,2.54c-1.3," +
                    "1.79 -2.76,3.51 -4.41,5.15 -8.02,8.02 -17.71,12.03 -29.06,12.03s-20.97," +
                    "-4.01 -28.99,-12.03c-8.02,-8.02 -12.03,-17.71 -12.03,-29.06s4.01," +
                    "-20.97 12.03,-28.99 17.68,-12.03 28.99,-12.03c9.71,0 18.2,2.91 25.48," +
                    "8.74 1.2,1 2.39,2.09 3.59,3.29 0.45,0.45 0.87,0.9 1.27,1.34l-5.08," +
                    "5.08 -47.81,47.81ZM61.33,192.23c-5.83,-4.48 -12.6,-6.72 -20.32,-6.72 -9.31," +
                    "0 -17.28,3.3 -23.91,9.9 -6.62,6.6 -9.94,14.58 -9.94,23.94 0,7.72 2.24," +
                    "14.49 6.72,20.32l47.44,-47.44Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m99.14,201.34v57h-6.72v-73.36h6.72v4.71c6.13," +
                    "-6.08 13.25,-9.11 21.37,-9.11 8.67,0 15.71,2.72 21.14,8.14 5.43,5.43 8.22," +
                    "12.28 8.37,20.54v49.08h-6.72v-48.48c0,-6.23 -2.25,-11.54 -6.76," +
                    "-15.95 -4.51,-4.41 -9.85,-6.61 -16.02,-6.61s-11.5,2.17 -15.99,6.5c-2.44," +
                    "2.44 -4.23,4.96 -5.38,7.55Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m173.85,63.3h5.3v125.5c5.68,-5.48 12.5,-8.22 20.47," +
                    "-8.22 8.67,0 15.71,2.72 21.14,8.14 5.43,5.43 8.22,12.28 8.37," +
                    "20.54v49.08h-6.72v-48.48c0,-6.23 -2.25,-11.54 -6.76,-15.95 -4.51," +
                    "-4.41 -9.85,-6.61 -16.02,-6.61s-11.5,2.17 -15.99,6.5c-1.99,1.99 -3.49," +
                    "4.06 -4.48,6.2v58.35h-7.58V66.72c0.85,-1.89 0.39,-3.43 2.28,-3.43Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m330.73,258.34h-6.72v-14.94c-1.3,1.74 -2.71," +
                    "3.41 -4.26,5.01 -8.02,8.02 -17.71,12.03 -29.06,12.03s-20.97,-4.01 -28.99," +
                    "-12.03c-8.02,-8.02 -12.03,-17.68 -12.03,-28.99s4.01,-21.04 12.03," +
                    "-29.06c8.02,-8.02 17.68,-12.03 28.99,-12.03s21.04,4.01 29.06,12.03c1.54," +
                    "1.59 2.96,3.26 4.26,5.01v-10.38h6.72v73.36ZM324.01,226.07v-13.3c-1.25," +
                    "-6.57 -4.36,-12.35 -9.34,-17.33 -6.62,-6.62 -14.62,-9.94 -23.98," +
                    "-9.94s-17.36,3.31 -23.98,9.94c-6.62,6.62 -9.94,14.62 -9.94,23.98s3.31," +
                    "17.34 9.94,23.94c6.62,6.6 14.62,9.9 23.98,9.9s17.36,-3.31 23.98,-9.94c4.98," +
                    "-4.98 8.09,-10.73 9.34,-17.26Z"),
            fill = SolidColor(primaryColor)
        )
        addPath(
            pathData = addPathNodes("m359.86,201.34v57h-6.72v-73.36h6.72v4.71c6.13," +
                    "-6.08 13.25,-9.11 21.37,-9.11 8.67,0 15.71,2.72 21.14,8.14 5.43,5.43 8.22," +
                    "12.28 8.37,20.54v49.08h-6.72v-48.48c0,-6.23 -2.25,-11.54 -6.76,-15.95 -4.51," +
                    "-4.41 -9.85,-6.61 -16.02,-6.61s-11.5,2.17 -15.99,6.5c-2.44,2.44 -4.23," +
                    "4.96 -5.38,7.55Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m496.06,240.71l7.1,2.54c-1.3,1.79 -2.76,3.51 -4.41," +
                    "5.15 -8.02,8.02 -17.71,12.03 -29.06,12.03s-20.97,-4.01 -28.99,-12.03c-8.02," +
                    "-8.02 -12.03,-17.71 -12.03,-29.06s4.01,-20.97 12.03,-28.99c8.02," +
                    "-8.02 17.68,-12.03 28.99,-12.03s21.04,4.01 29.06,12.03c1.64,1.64 3.11," +
                    "3.34 4.41,5.08l-7.1,2.61c-0.75,-0.9 -1.54,-1.79 -2.39,-2.69 -6.62," +
                    "-6.57 -14.62,-9.86 -23.98,-9.86s-17.28,3.3 -23.91,9.9c-6.62,6.6 -9.94," +
                    "14.58 -9.94,23.94s3.31,17.36 9.94,23.98c6.62,6.62 14.59,9.94 23.91," +
                    "9.94s17.36,-3.31 23.98,-9.94c0.85,-0.85 1.64,-1.72 2.39,-2.61Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m522.73,223.93c0.39,8.56 3.66,15.92 9.83,22.08 6.62," +
                    "6.62 14.61,9.93 23.98,9.93 1.2,0 2.38,-0.05 3.54,-0.16l3.22,6.81c-2.18," +
                    "0.35 -4.44,0.53 -6.76,0.53 -11.34,0 -21.02,-4.01 -29.05,-12.04 -8,-8 -11.99," +
                    "-17.66 -11.99,-29 0,-11.34 4.01,-21.03 12.04,-29.05 7.99,-7.99 17.66," +
                    "-11.99 29,-11.99 11.34,0 21.01,4 29,11.99 6.87,6.87 10.81,14.93 11.83," +
                    "24.19 0.14,1.55 0.21,3.17 0.21,4.86 0,0.63 -0.02,1.25 -0.05," +
                    "1.85h-7.18s-67.62,0 -67.62,0ZM590.08,217.22c-0.95,-7.29 -4.16,-13.66 -9.61," +
                    "-19.12 -6.59,-6.59 -14.55,-9.89 -23.9,-9.9 -9.35,-0.02 -17.34,3.28 -23.96," +
                    "9.9 -5.46,5.46 -8.66,11.83 -9.61,19.12h67.09Z"),
            fill = SolidColor(onSurfaceColor)
        )
        addPath(
            pathData = addPathNodes("m688.2,258.34h-6.72v-14.94c-1.3,1.74 -2.71," +
                    "3.41 -4.26,5.01 -8.02,8.02 -17.71,12.03 -29.06,12.03s-20.97,-4.01 -28.99," +
                    "-12.03c-8.02,-8.02 -12.03,-17.68 -12.03,-28.99s4.01,-21.04 12.03,-29.06c8.02," +
                    "-8.02 17.68,-12.03 28.99,-12.03s21.04,4.01 29.06,12.03c1.54,1.59 2.96," +
                    "3.26 4.26,5.01v-105.07h6.72v168.05ZM681.48,226.07v-13.3c-1.25,-6.57 -4.36," +
                    "-12.35 -9.34,-17.33 -6.62,-6.62 -14.62,-9.94 -23.98,-9.94s-17.36," +
                    "3.31 -23.98,9.94c-6.62,6.62 -9.94,14.62 -9.94,23.98s3.31,17.34 9.94," +
                    "23.94c6.62,6.6 14.62,9.9 23.98,9.9s17.36,-3.31 23.98,-9.94c4.98," +
                    "-4.98 8.09,-10.73 9.34,-17.26Z"),
            fill = SolidColor(onSurfaceColor)
        )
        build()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldHomeCenteredAppBar(scrollBehavior: TopAppBarScrollBehavior
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldTweaksAppBar(
    navController: NavController,
    screen: String,
    showBackIcon: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
        ),
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = screen,
                style = MaterialTheme.typography.titleLarge
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
