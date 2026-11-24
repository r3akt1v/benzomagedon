package com.benzomageddon.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.benzomageddon.ui.screens.CalendarScreen
import com.benzomageddon.ui.screens.HomeScreen
import com.benzomageddon.ui.screens.SettingsScreen
import com.benzomageddon.ui.screens.StatsScreen
import com.benzomageddon.ui.screens.TransactionFormScreen

object Routes {
    const val HOME = "home"
    const val STATS = "stats"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val ADD = "add"
    const val EDIT = "transaction/{id}"
    fun edit(id: Int) = "transaction/$id"
}

private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Главная", Icons.Default.Home),
    Tab(Routes.STATS, "Статистика", Icons.Default.BarChart),
    Tab(Routes.CALENDAR, "Календарь", Icons.Default.CalendarMonth),
    Tab(Routes.SETTINGS, "Настройки", Icons.Default.Settings)
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in tabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onAddClick = { navController.navigate(Routes.ADD) },
                    onEditClick = { id -> navController.navigate(Routes.edit(id)) }
                )
            }
            composable(Routes.STATS) { StatsScreen() }
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onTransactionClick = { id -> navController.navigate(Routes.edit(id)) }
                )
            }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(Routes.ADD) {
                TransactionFormScreen(
                    transactionId = null,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                Routes.EDIT,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                TransactionFormScreen(
                    transactionId = entry.arguments?.getInt("id"),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
