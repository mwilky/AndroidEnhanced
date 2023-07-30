package com.mwilky.androidenhanced

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mwilky.androidenhanced.ui.theme.AndroidEnhancedTheme

class MainActivity : ComponentActivity() {

    companion object {
        val TAG = "DEBUG: Android Enhanced"
        val DEBUG = true
    }

    private lateinit var dataStoreManager : DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)
        setContent {
            AndroidEnhancedTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   Navigation(this@MainActivity)
                }
            }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController : NavController, context : Context) {
    // Pressing back on Homescreen should close the app, rather than going back to onboarding
    HandleBackButton {
        (context as? Activity)?.finish()
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior= scrollBehavior,
                title = {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Android")
                            }

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                                append(" Enhanced")
                            }
                        }
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
            CustomList(paddingValues = it)
        }
    )

}

@Composable
fun CustomList(paddingValues: PaddingValues) {
    val numbers = remember { mutableStateListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)}

    LazyColumn(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
        items(items = numbers, key = {it.hashCode() }) {

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



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AndroidEnhancedTheme {
        val navController = rememberNavController()
        // Get the context using LocalContext
        val context = LocalContext.current
        HomeScreen(navController, context)
    }
}