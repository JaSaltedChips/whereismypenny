package app.harsha.wheresmypenny.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.harsha.wheresmypenny.R
import app.harsha.wheresmypenny.ui.screen.add.AddTransactionScreen
import app.harsha.wheresmypenny.ui.screen.overview.OverviewScreen
import app.harsha.wheresmypenny.ui.screen.reports.ReportsScreen
import app.harsha.wheresmypenny.ui.screen.settings.SettingsScreen
import app.harsha.wheresmypenny.ui.screen.transactions.TransactionsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.action_add_transaction),
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Overview.route,
        ) {
            composable(Screen.Overview.route)       { OverviewScreen(innerPadding) }
            composable(Screen.Transactions.route)   { TransactionsScreen(innerPadding) }
            composable(Screen.AddTransaction.route) { AddTransactionScreen(navController, innerPadding) }
            composable(Screen.Reports.route)        { ReportsScreen(innerPadding) }
            composable(Screen.Settings.route)       { SettingsScreen(innerPadding) }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = backStackEntry?.destination

    NavigationBar {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = currentDest?.hierarchy?.any { it.route == item.screen.route } == true,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon  = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) },
            )
        }
    }
}

private enum class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    OVERVIEW(Screen.Overview,     Icons.Filled.Home,     R.string.nav_overview),
    TRANSACTIONS(Screen.Transactions, Icons.Filled.List, R.string.nav_transactions),
    REPORTS(Screen.Reports,       Icons.Filled.BarChart, R.string.nav_reports),
    SETTINGS(Screen.Settings,     Icons.Filled.Settings, R.string.nav_settings),
}
