package com.mwilky.androidenhanced.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mwilky.androidenhanced.DataStoreManager
import com.mwilky.androidenhanced.dataclasses.EnvironmentProp
import com.mwilky.androidenhanced.dataclasses.TweaksCard
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, context: Context) {
    //Top App Bar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                ),
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
            HomeScreenScrollableContent(topPadding = it, navController)
        }
    )
}

@Composable
fun HomeScreenScrollableContent(topPadding: PaddingValues, navController: NavController ) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding.calculateTopPadding())
    ) {
        // Header Logo
        item {
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
                        value = /*if (isPremiumFeatures) "Unlocked" else*/ "Activated"
                    ),
                    EnvironmentProp(
                        icon = Icons.Outlined.Create,
                        label = "Premium:",
                        value = /*if (isPremiumFeatures) "Unlocked" else*/ "Locked"
                    )
                )
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Statusbar",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Quicksettings",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Notifications",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Lockscreen",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Buttons",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Miscellaneous",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Placeholder",
                ),
                navController = navController
            )
        }

        item {
            TweaksItem(
                card = TweaksCard(
                    icon = Icons.Outlined.Build,
                    label = "Placeholder",
                ),
                navController = navController
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
            text = "$greetingMessage, $name",
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
            EnvironmentItem(environmentProp)
        }
    }
}
@Composable
fun EnvironmentItem(
    prop: EnvironmentProp,
    modifier: Modifier = Modifier
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
            lineHeight = 10.sp,
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
    BoxWithConstraints(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = {
                        navController.navigate(Screen.Tweaks.withArgs(card.label))
                    }
                ),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = card.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = card.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "View",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 32.dp)
                    )
                }
            }
        }
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
