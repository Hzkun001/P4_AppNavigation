package id.antasari.p4appnavigation_230104040118.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import id.antasari.p4appnavigation_230104040118.R
import id.antasari.p4appnavigation_230104040118.screens.*
import id.antasari.p4appnavigation_230104040118.ui.theme.ThemeController
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.animation.Crossfade
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val nav = rememberNavController()

    Scaffold(
        topBar = {
            // variabel yang dibutuhkan TopAppBar
            val canNavigateBack = nav.previousBackStackEntry != null
            val routeNow = nav.currentBackStackEntryAsState().value?.destination?.route.orEmpty()
            val isHome = routeNow == Route.Home.path
            TopAppBar(
                title = {
                    if (isHome) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav),
                                contentDescription = "Nav",
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.app_name),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    } else {
                        // Layar lain: judul dinamis
                        Text(
                            text = currentTitle(nav),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { ThemeController.isDark = !ThemeController.isDark }) {
                        Crossfade(targetState = ThemeController.isDark) { dark ->
                            Icon(
                                imageVector = if (dark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle theme",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = nav,
            startDestination = Route.Home.path
        ) {
            // 🏠 Home
            composable(Route.Home.path) { HomeScreen(nav) }

            composable(Route.ActivityA.path) {
                ActivityAScreen(onOpen = { nav.navigate(Route.ActivityB.path) })
            }
            composable(Route.ActivityB.path) {
                ActivityBScreen()
            }
            composable(Route.ActivityC.path) {
                ActivityCScreen(
                    onSend = { name, nim ->
                        nav.navigate(Route.ActivityD.make(name, nim))
                    }
                )
            }
            composable(
                route = Route.ActivityD.path,
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("studentId") { type = NavType.StringType }
                )
            ) { backStack ->
                val name = Uri.decode(backStack.arguments?.getString("name") ?: "")
                val nim = Uri.decode(backStack.arguments?.getString("studentId") ?: "")
                ActivityDScreen(name = name, nim = nim, onResend = { nav.popBackStack() }
                )
            }
            // 🧭 Back Stack demo
            composable(Route.Step1.path) {
                StepScreen(
                    step = 1,
                    onNext = { nav.navigate(Route.Step2.path) },
                    onClearToHome = {
                        nav.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Route.Step2.path) {
                StepScreen(
                    step = 2,
                    onNext = { nav.navigate(Route.Step3.path) },
                    onClearToHome = {
                        nav.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Route.Step3.path) {
                StepScreen(
                    step = 3,
                    onNext = null,
                    onClearToHome = {
                        nav.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            // 🧩 Hub (nested graph)
            navigation(startDestination = Route.HubDashboard.path, route = Route.Hub.path) {
                composable(Route.HubDashboard.path) { HubScreen(nav, HubTab.Dashboard) }
                composable(Route.HubMessages.path) { HubScreen(nav, HubTab.Messages) }
                composable(Route.HubProfile.path) { HubScreen(nav, HubTab.Profile) }
                composable(
                    Route.HubMsgDetail.path,
                    listOf(navArgument("id") { type = NavType.StringType })
                ) { backStack ->
                    val id = backStack.arguments?.getString("id").orEmpty()
                    MessageDetailScreen(id = id, onBack = { nav.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun currentTitle(nav: NavHostController): String {
    val route = nav.currentBackStackEntryAsState().value?.destination?.route ?: ""
    return when {
        route.startsWith("hub") -> "Activity + Fragment Hub"
        route.startsWith("activity_d") -> "Activity D – Data Display"
        route == Route.ActivityC.path -> "Activity C – Send Data"
        route == Route.ActivityA.path -> "Activity A"
        route == Route.ActivityB.path -> "Launched by Intent"
        route == Route.Step1.path -> "Step 1 of 3"
        route == Route.Step2.path -> "Step 2 of 3"
        route == Route.Step3.path -> "Step 3 of 3"
        else -> "Navigation Lab"
    }
}
